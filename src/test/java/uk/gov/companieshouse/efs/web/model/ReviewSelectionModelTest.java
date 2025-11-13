package uk.gov.companieshouse.efs.web.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReviewSelectionModelTest {

    @Test
    void constructorCopiesConfirmedFieldFromOriginalObject() {
        ReviewSelectionModel original = new ReviewSelectionModel();
        original.setConfirmed("yes");

        ReviewSelectionModel copy = new ReviewSelectionModel(original);

        assertEquals("yes", copy.getConfirmed());
    }

    @Test
    void constructorHandlesNullConfirmedFieldGracefully() {
        ReviewSelectionModel original = new ReviewSelectionModel();
        original.setConfirmed(null);

        ReviewSelectionModel copy = new ReviewSelectionModel(original);

        assertNull(copy.getConfirmed());
    }

    @Test
    void equalsReturnsTrueForObjectsWithSameConfirmedValue() {
        ReviewSelectionModel model1 = new ReviewSelectionModel();
        model1.setConfirmed("yes");

        ReviewSelectionModel model2 = new ReviewSelectionModel();
        model2.setConfirmed("yes");

        assertEquals(model1, model2);
    }

    @Test
    void equalsReturnsFalseForObjectsWithDifferentConfirmedValues() {
        ReviewSelectionModel model1 = new ReviewSelectionModel();
        model1.setConfirmed("yes");

        ReviewSelectionModel model2 = new ReviewSelectionModel();
        model2.setConfirmed("no");

        assertNotEquals(model1, model2);
    }

    @Test
    void hashCodeIsConsistentWithEqualsForSameConfirmedValue() {
        ReviewSelectionModel model1 = new ReviewSelectionModel();
        model1.setConfirmed("yes");

        ReviewSelectionModel model2 = new ReviewSelectionModel();
        model2.setConfirmed("yes");

        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void toStringReturnsExpectedFormat() {
        ReviewSelectionModel model = new ReviewSelectionModel();
        model.setConfirmed("yes");

        String expected = "ReviewSelectionModel[confirmed=yes]";
        assertEquals(expected, model.toString());
    }
}