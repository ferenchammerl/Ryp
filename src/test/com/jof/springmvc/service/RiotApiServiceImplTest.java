package com.jof.springmvc.service;

import net.rithms.riot.dto.Summoner.Rune;
import net.rithms.riot.dto.Summoner.RunePage;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Ferenc_S on 12/10/2016.
 */
public class RiotApiServiceImplTest {

    @org.junit.Test
    public void testUserHasRunePageMock() throws Exception {
        for (RunePage page : getRunePages()) {
            System.out.println(page.getName());
        }
    }

    Set<RunePage> getRunePages() {
        Set<RunePage> pages = new TreeSet<>();
        RunePage page;

        page = new RunePage();
        when(page.getName()).thenReturn("RunePage 1");
        pages.add(page);

        page = new RunePage();
        when(page.getName()).thenReturn("RunePage 3");
        pages.add(page);

        page = new RunePage();
        when(page.getName()).thenReturn("RunePage 2");
        pages.add(page);

        return pages;
    }

}