package com.dbottillo.mtgsearchfree.util;

import com.dbottillo.mtgsearchfree.exceptions.ExceptionCode;
import com.dbottillo.mtgsearchfree.exceptions.MTGException;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class MTGExceptionMatcher extends TypeSafeMatcher<MTGException> {

    private ExceptionCode expectedCode;
    private ExceptionCode foundCode;

    public MTGExceptionMatcher(ExceptionCode exceptionCode){
        expectedCode = exceptionCode;
    }

    public static MTGExceptionMatcher hasCode(ExceptionCode code){
        return new MTGExceptionMatcher(code);
    }

    @Override
    protected boolean matchesSafely(MTGException item) {
        foundCode = item.getCode();
        return expectedCode == foundCode;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(foundCode)
                .appendText("was not found instead of")
                .appendValue(expectedCode);

    }
}
