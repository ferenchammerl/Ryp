package com.jof.springmvc.controller;

import static com.jof.springmvc.service.MockUserService.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.jof.springmvc.model.Role;
import com.jof.springmvc.model.User;
import com.jof.springmvc.service.MockRoleService;
import com.jof.springmvc.service.MockUserService;
import com.jof.springmvc.service.RoleService;
import com.jof.springmvc.service.UserService;

/**
 * Created by Ferenc_S on 12/13/2016.
 * Edited by Johannes.
 */ 
@ContextConfiguration
public class UserControllerTest {
	
    @Spy
    UserService service = new MockUserService();
    
    @Spy
    RoleService roleService = new MockRoleService();

    @InjectMocks
    UserController userController;
    
    @Spy
    ModelMap model;
    
    @Mock
    Authentication authentication;
    @Mock
    BindingResult bindingResult;

    User user;
    SecurityContext securityContext;
    MockHttpSession session;
    MockHttpServletRequest request;

    @Before
    public void setUp() {
    	authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("Anonymous");
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        bindingResult = mock(BindingResult.class);
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        request.setSession(session);
        MockitoAnnotations.initMocks(this);
    }
    
    /*
     * Creates an admin
     */
	private User createAdmin() {
    	Role adminRole = new Role();
    	adminRole.setId(1);
    	adminRole.setType(ROLE_ADMIN);
    	
    	User adminUser = new User();
        adminUser.setId(ID);
        adminUser.setUsername(ADMIN_NAME);
        adminUser.setEmail(ADMIN_EMAIL);
        adminUser.setPassword(ADMIN_PASSWORD);
        adminUser.setRoles(new HashSet<Role>(Arrays.asList(adminRole)));
        
        return adminUser;
	}
    
	 /*
     * Creates a regular user.
     */
    private User createUser() {
    	Role userRole = new Role();
        userRole.setId(1);
        userRole.setType(ROLE_USER);
        
        User userUser = new User();
        userUser.setId(ID+1);
        userUser.setUsername(USER_NAME);
        userUser.setEmail(USER_EMAIL);
        userUser.setPassword(USER_PASSWORD);
        userUser.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        
        return userUser;
    }
    
    /*
     * Sets correct user type to user field and as session attribute.
     */
    private void asRole(String roleType) {
		switch (roleType) {
		case ROLE_ADMIN:
			user = createAdmin();
			break;
		case ROLE_USER:
			user = createUser();
			break;
		default:
			break;
		}
		request.getSession().setAttribute("remoteUser", user);
    }

/*
 * Controller to XML Mapping and access security tests
 */
    
    
    /*
     * Test access level for anonymous and assert correct return values
     */

    @Test
    public void testAnonymousGetLogin() throws Exception {
    	AuthenticationTrustResolverImpl authenticationTrustResolver = mock(AuthenticationTrustResolverImpl.class);
    	when(authenticationTrustResolver.isAnonymous(authentication)).thenReturn(true);
    	userController.authenticationTrustResolver = authenticationTrustResolver;
    	when(userController.isCurrentAuthenticationAnonymous()).thenReturn(true);
    	assertEquals("login", userController.loginPage(request));
    }
    
    @Test
    public void testAnonymousGetRegister() throws Exception {
    	assertEquals("register", userController.registerUser(model, request));
    	assertNotNull(model.get("user"));
    	assertEquals(null, ((User) model.get("user")).getId());
    }
    
    @Test
    public void testAnonymousPostRegister() throws Exception {
    	User newUser = new User();
    	newUser.setId(13337L);
    	newUser.setUsername(UUID.randomUUID().toString()); // something that's not taken
    	assertEquals("login", userController.registerUser(newUser, bindingResult, model, request));
    }
    
    
    /*
     * Test access level for USER role and assert correct return values
     */
    
    @Test
    public void testGetUserLoggedInLogin() throws Exception {
    	asRole(ROLE_USER);
    	AuthenticationTrustResolverImpl authenticationTrustResolver = mock(AuthenticationTrustResolverImpl.class);
    	when(authenticationTrustResolver.isAnonymous(authentication)).thenReturn(true);
    	userController.authenticationTrustResolver = authenticationTrustResolver;
    	assertEquals("redirect:/", userController.loginPage(request));
    }
    
    @Test
    public void testUserGetLogout() throws Exception {
    	asRole(ROLE_USER);
    	userController.persistentTokenBasedRememberMeServices = mock(PersistentTokenBasedRememberMeServices.class);
    	assertEquals("redirect:/login?logout", userController.logoutPage(request, null));
    }
    
    @Test (expected=AccessDeniedException.class)
    public void testUserGetListUsers() throws Exception {
    	asRole(ROLE_USER);
    	userController.listUsers(model, request);
    }
    
    @Test (expected=AccessDeniedException.class)
    public void testUserGetEditUser() throws Exception {
    	asRole(ROLE_USER);
    	userController.editUser("LeagueGuy1", model, request);
    }
    
    @Test (expected=AccessDeniedException.class)
    public void testUserPostUpdateUser() throws Exception {
    	asRole(ROLE_USER);
    	User userUser = new User();
    	userUser.setId(1338L);
    	userUser.setUsername("LeagueGuy1");
    	userController.updateUser(userUser, bindingResult, model, "LeagueGuy1", request);
    }
    
    @Test (expected=AccessDeniedException.class)
    public void testUserGetDeleteUser() throws Exception {
    	asRole(ROLE_USER);
    	User userUser = new User();
    	userUser.setId(1338L);
    	userUser.setUsername("LeagueGuy1");
    	userController.deleteUser("LeagueGuy1", request);
    }

    
    /*
     * Test access level for ADMIN role and assert correct return values
     */
    @Test
    public void testAdminGetListUsers() throws Exception {
    	asRole(ROLE_ADMIN);
    	assertEquals("userlist", userController.listUsers(model, request));
    	assertEquals(4, service.findAllUsersButMe(user).size());
        assertEquals(model.get("users"), service.findAllUsersButMe(user));
        verify(service, atLeastOnce()).findAllUsersButMe(user);
        verify(service, atLeastOnce()).findAllUsersButMe(user);
    }

    @Test
    public void testAdminGetEditUser() throws Exception {
    	asRole(ROLE_ADMIN);
    	assertEquals("newUser", userController.editUser("LeagueGuy1", model, request));
    }
    
    @Test
    public void testAdminPostUpdateUser() throws Exception {
    	asRole(ROLE_ADMIN);
    	User userUser = new User();
    	userUser.setId(1338L);
    	userUser.setUsername("LeagueGuy1");
    	assertEquals("redirect:/list?createdUser=LeagueGuy1", userController.updateUser(userUser, bindingResult, model, "LeagueGuy1", request));
    }
    
    @Test
    public void testAdminGetDeleteUser() throws Exception {
    	asRole(ROLE_ADMIN);
    	User userUser = new User();
    	userUser.setId(1338L);
    	userUser.setUsername("LeagueGuy1");
    	assertEquals("redirect:/list", userController.deleteUser("LeagueGuy1", request));
    	assertTrue(service.findByUserName(userUser.getUsername()) == null);
    }
    
    @Test
    public void testAdminGetLogout() throws Exception {
    	asRole(ROLE_ADMIN);
    	userController.persistentTokenBasedRememberMeServices = mock(PersistentTokenBasedRememberMeServices.class);
    	assertEquals("redirect:/login?logout", userController.logoutPage(request, null));
    }
    

}