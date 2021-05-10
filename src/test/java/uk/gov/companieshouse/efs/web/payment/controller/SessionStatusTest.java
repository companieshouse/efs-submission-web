package uk.gov.companieshouse.efs.web.payment.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionStatusTest {

    @Test
    void fromValueWhenMatched() {
        assertThat(SessionStatus.fromValue("paid"), is(SessionStatus.PAID));
    }
    
    @Test
    void fromValueWhenNotMatched() {
        assertThat(SessionStatus.fromValue("unmatched"), is(nullValue()));
    }

    @Test
    void testToString() {
        assertThat(SessionStatus.PENDING.toString(), is("pending"));
    }
}