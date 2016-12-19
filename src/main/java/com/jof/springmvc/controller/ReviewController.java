package com.jof.springmvc.controller;

import com.jof.springmvc.form.CommentForm;
import com.jof.springmvc.form.ReviewForm;
import com.jof.springmvc.model.Comment;
import com.jof.springmvc.model.Review;
import com.jof.springmvc.model.User;
import com.jof.springmvc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user/reviews")
public class ReviewController {

    @Autowired
    UserService userService;

    @Autowired
    ReviewService reviewService;

    @Autowired
    CommentService commentService;

    @Autowired
    RoleService roleService;
    @Autowired
    RiotApiService riotApiService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;

    @Autowired
    AuthenticationTrustResolver authenticationTrustResolver;

    /**
     * This method will list all existing users.
     */
    @RequestMapping(value = {"/list"}, method = RequestMethod.GET)
    public String listUsers(ModelMap model, HttpServletRequest request) {

        User remoteUser = new User();

        if (request.getSession().getAttribute("remoteUser") != null) {
            remoteUser = (User) request.getSession().getAttribute("remoteUser");
        }

        List<Review> reviews = reviewService.findAllReviewByUser(remoteUser);

        userService.callStoredProcedureTest();

        model.addAttribute("reviews", reviews);

        return "reviewList";
    }

    /**
     * This method will be called on form submission, handling POST request for
     * registering a new user in the database. It also validates the user input
     */
    @RequestMapping(value = {"/review{reviewId}"}, method = RequestMethod.GET)
    public String getReviewById(@PathVariable String reviewId, HttpServletRequest request, ModelMap model) {

        User remoteUser = new User();

        if (request.getSession().getAttribute("remoteUser") != null) {
            remoteUser = (User) request.getSession().getAttribute("remoteUser");
        }

        Review review = reviewService.findById(Integer.valueOf(reviewId));

        if (!review.getSource_user_id().getId().equals(remoteUser.getId())) {
            return "redirect:/user/reviews/list";
        }

        List<CommentForm> commentForms = commentService.findAllCommentFormsForReview(review);

        CommentForm cf = new CommentForm();


        model.addAttribute("review", review);
        model.addAttribute("commentForms", commentForms);
        model.addAttribute("commentForm", cf);

        return "review";
    }

    @RequestMapping(value = {"/review{reviewId}"}, method = RequestMethod.POST)
    public String createComment(@PathVariable String reviewId, CommentForm ncf, HttpServletRequest request, ModelMap model) {

        Comment comment = new Comment();

        comment.setActive(true);
        comment.setBody(ncf.getBody());
        comment.setCreated_by((User) request.getSession().getAttribute("remoteUser"));
        comment.setReview_id(reviewService.findById(Integer.valueOf(reviewId)));

        commentService.saveComment(comment);

        return "redirect:/user/reviews/review" + reviewId;
    }

    /**
     * This method will be called on form submission, handling POST request for
     * registering a new user in the database. It also validates the user input
     */
    @RequestMapping(value = {"/deleteReview{reviewId}"}, method = RequestMethod.GET)
    public String deleteReviewById(@PathVariable String reviewId, HttpServletRequest request) {

        User remoteUser = new User();

        if (request.getSession().getAttribute("remoteUser") != null) {
            remoteUser = (User) request.getSession().getAttribute("remoteUser");
        }

        // TODO: CHECK IF USER IS THE AUTHOR OF THE REVIEW

        Review review = reviewService.findById(Integer.valueOf(reviewId));

        if (!review.getSource_user_id().getId().equals(remoteUser.getId())) {
            return "redirect:/user/reviews/list";
        }

        reviewService.deleteById(Integer.valueOf(reviewId));

        return "redirect:/user/reviews/list";
    }

    /**
     * This method will be called on form submission, handling POST request for
     * saving user in database. It also validates the user input
     */
    @RequestMapping(value = {"/createReview"}, method = RequestMethod.POST)
    public String saveReview(@Valid ReviewForm rf, BindingResult result) {

        Review review = new Review();

        review.setGame_id(rf.getGame_id());
        review.setBody(rf.getBody());
        review.setTitle(rf.getTitle());
        review.setSource_user_id(userService.findById(rf.getSource_user_id()));
        review.setTarget_user_id(Long.valueOf(rf.getTarget_user_id()));
        review.setActive(true);

        reviewService.saveReview(review);

        return "redirect:/user/reviews/list";
    }

    @RequestMapping(value = {"/createReview"}, method = RequestMethod.GET)
    public String listIncomingFriendRequests(ModelMap model, HttpServletRequest request, @RequestParam Map<String, String> requestParams) {

        User remoteUser = new User();

        if (request.getSession().getAttribute("remoteUser") != null) {
            remoteUser = (User) request.getSession().getAttribute("remoteUser");
        }

        ReviewForm reviewForm = new ReviewForm();

        long game_id = Long.parseLong(requestParams.get("game_id"));
        long target_user_id = Long.parseLong(requestParams.get("target_user_id"));
        reviewForm.setGame_id(Long.valueOf(game_id));
        reviewForm.setSource_user_id(Long.valueOf(remoteUser.getId()));
        reviewForm.setTarget_user_id(Long.valueOf(target_user_id));

        model.addAttribute("reviewForm", reviewForm);

        return "createReview";
    }
}