package uk.gov.companieshouse.efs.web.categorytemplates.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.efs.categorytemplates.CategoryTemplateApi;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.function.Predicate;

@ExtendWith(MockitoExtension.class)
class CategoryTemplateModelTest {

    public static final String CT_01 = "CT01";
    public static final String CT_02 = "CT02";
    public static final String CT_03 = "CT03";
    public static final int LIMIT = 2;
    private CategoryTemplateModel testCategoryTemplate;

    private CategoryTemplateApi categoryTemplate;

    @BeforeEach
    void setUp() {
        categoryTemplate = new CategoryTemplateApi("CC01", "CategoryC01", "INS", "CC01", null);
        testCategoryTemplate = new CategoryTemplateModel(categoryTemplate);
    }

    @Test
    void defaultConstructor() {
        testCategoryTemplate = new CategoryTemplateModel();

        assertThat(testCategoryTemplate.getDetails(), is(new CategoryTemplateApi("", "", null, null, null)));
    }

    @Test
    void setGetDetails() {
        CategoryTemplateApi expected = new CategoryTemplateApi("CC01", "CategoryC01", "INS", "CC01", null);

        testCategoryTemplate.setDetails(expected);

        assertThat(testCategoryTemplate.getDetails(), is(expected));
    }

    @Test
    void setTestCategoryTemplateList() {
        testCategoryTemplate.setCategoryTemplateList(null);

        assertThat(testCategoryTemplate.getCategoryTemplateList(), is(Collections.emptyList()));
    }

    @Test
    void getCategoryStackReturnsEmptyDequeWhenStackIsEmpty() {
        testCategoryTemplate = new CategoryTemplateModel();

        assertThat(testCategoryTemplate.getCategoryStack().isEmpty(), is(true));
    }

    @Test
    void rewindCategoryStackReturnsNullWhenStackIsEmpty() {
        testCategoryTemplate = new CategoryTemplateModel();

        CategoryTemplateApi result = testCategoryTemplate.rewindCategoryStack(CT_01);

        assertThat(result, is(nullValue()));
    }

    @Test
    void rewindCategoryStackReturnsNullWhenCategoryTypeIsAtTopOfStack() {
        CategoryTemplateApi category = new CategoryTemplateApi(CT_01, "Category1", "INS", CT_01, null);
        testCategoryTemplate.pushCategory(category);

        CategoryTemplateApi result = testCategoryTemplate.rewindCategoryStack(CT_01);

        assertThat(result, is(nullValue()));
    }

    @Test
    void rewindCategoryStackRemovesAndReturnsLastCategoryWhenCategoryTypeNotFound() {
        CategoryTemplateApi category1 = new CategoryTemplateApi(CT_01, "Category1", "INS", CT_01, null);
        CategoryTemplateApi category2 = new CategoryTemplateApi(CT_02, "Category2", "INS", CT_02, null);
        testCategoryTemplate.pushCategory(category1);
        testCategoryTemplate.pushCategory(category2);

        assertThat(testCategoryTemplate.getCategoryStack().contains(category2), is(true));
    }

    @Test
    void setGetCategoryType() {
        testCategoryTemplate.setCategoryType(null);

        assertThat(testCategoryTemplate.getCategoryType(), is(nullValue()));
    }

    @Test
    void setGetCategoryName() {
        testCategoryTemplate.setCategoryName(null);

        assertThat(testCategoryTemplate.getCategoryName(), is(nullValue()));
    }

    @Test
    void setGetCategoryNumber() {
        testCategoryTemplate.setCompanyNumber(null);

        assertThat(testCategoryTemplate.getCompanyNumber(), is(nullValue()));
    }

    @Test
    void setGetCategorySubmissionId() {
        testCategoryTemplate.setSubmissionId(null);

        assertThat(testCategoryTemplate.getSubmissionId(), is(nullValue()));
    }

    @Test
    void setGetCategoryHint() {
        testCategoryTemplate.setCategoryHint(null);

        assertThat(testCategoryTemplate.getCategoryHint(), is(nullValue()));
    }

    @Test
    void setGetParent() {
        testCategoryTemplate.setParent(null);
        assertThat(testCategoryTemplate.getParent(), is(nullValue()));
    }

    @Test
    void distinctByKeyReturnsTrueForUniqueKeys() {
        Predicate<String> predicate = CategoryTemplateModel.distinctByKey(String::length);

        assertThat(predicate.test("one"), is(true));
        assertThat(predicate.test("three"), is(true));
    }

    @Test
    void getParentCategoryReturnsRootCategoryWhenStackIsEmpty() {
        testCategoryTemplate = new CategoryTemplateModel();

        CategoryTemplateApi result = testCategoryTemplate.getParentCategory();

        assertThat(result, is(CategoryTemplateModel.ROOT_CATEGORY));
    }

    @Test
    void getParentCategoryReturnsLastCategoryWhenStackIsNotEmpty() {
        CategoryTemplateApi category = new CategoryTemplateApi(CT_01, "Category1", "INS", CT_01, null);
        testCategoryTemplate.pushCategory(category);

        CategoryTemplateApi result = testCategoryTemplate.getParentCategory();

        assertThat(result, is(category));
    }

    @Test
    void getCategorySequenceReturnsEmptyStringWhenStackIsEmpty() {
        testCategoryTemplate = new CategoryTemplateModel();

        String result = testCategoryTemplate.getCategorySequence(5);

        assertThat(result, is(""));
    }

    @Test
    void getCategorySequenceReturnsLimitedSequenceWhenLimitIsLessThanStackSize() {
        CategoryTemplateApi category1 = new CategoryTemplateApi(CT_01, "Category1", "INS", CT_01, null);
        CategoryTemplateApi category2 = new CategoryTemplateApi(CT_02, "Category2", "INS", CT_02, null);
        CategoryTemplateApi category3 = new CategoryTemplateApi(CT_03, "Category3", "INS", CT_03, null);
        testCategoryTemplate.pushCategory(category1);
        testCategoryTemplate.pushCategory(category2);
        testCategoryTemplate.pushCategory(category3);

        String result = testCategoryTemplate.getCategorySequence(LIMIT);

        assertThat(result, is("CT01,CT02"));
    }

    @Test
    void getCategorySequenceReturnsFullSequenceWhenLimitIsGreaterThanStackSize() {
        CategoryTemplateApi category1 = new CategoryTemplateApi(CT_01, "Category1", "INS", CT_01, null);
        CategoryTemplateApi category2 = new CategoryTemplateApi(CT_02, "Category2", "INS", CT_02, null);
        testCategoryTemplate.pushCategory(category1);
        testCategoryTemplate.pushCategory(category2);

        String result = testCategoryTemplate.getCategorySequence(5);

        assertThat(result, is("CT01,CT02"));
    }

    @Test
    void getCategorySequenceSkipsNullCategoryTypes() {
        CategoryTemplateApi category1 = new CategoryTemplateApi(CT_01, "Category1", "INS", CT_01, null);
        CategoryTemplateApi category2 = new CategoryTemplateApi(null, "Category2", "INS", CT_02, null);
        CategoryTemplateApi category3 = new CategoryTemplateApi(CT_03, "Category3", "INS", CT_03, null);
        testCategoryTemplate.pushCategory(category1);
        testCategoryTemplate.pushCategory(category2);
        testCategoryTemplate.pushCategory(category3);

        String result = testCategoryTemplate.getCategorySequence(3);

        assertThat(result, is("CT01,CT03"));
    }

    @Test
    void getCategorySequenceReturnsDistinctCategoryTypes() {
        CategoryTemplateApi category1 = new CategoryTemplateApi(CT_01, "Category1", "INS", CT_01, null);
        CategoryTemplateApi category2 = new CategoryTemplateApi(CT_01, "Category2", "INS", CT_02, null);
        CategoryTemplateApi category3 = new CategoryTemplateApi(CT_03, "Category3", "INS", CT_03, null);
        testCategoryTemplate.pushCategory(category1);
        testCategoryTemplate.pushCategory(category2);
        testCategoryTemplate.pushCategory(category3);

        String result = testCategoryTemplate.getCategorySequence(3);

        assertThat(result, is("CT01,CT03"));
    }

    @Test
    void toStringReturnsNonNullStringRepresentation() {
        String result = testCategoryTemplate.toString();

        assertThat(result, is(notNullValue()));
    }

    @Test
    void equalsReturnsFalseWhenComparingWithNull() {
        assertThat(testCategoryTemplate == null, is(false));
    }

    @Test
    void equalsReturnsFalseWhenComparingWithDifferentClass() {
        assertThat(testCategoryTemplate.equals(new Object()), is(false));
    }

    @Test
    void equalsReturnsFalseWhenCategoryStackIsDifferent() {
        CategoryTemplateModel other = new CategoryTemplateModel(categoryTemplate);
        other.pushCategory(new CategoryTemplateApi(CT_02, "Category2", "INS", CT_02, null));

        assertThat(testCategoryTemplate.equals(other), is(false));
    }

    @Test
    void equalsReturnsFalseWhenSubmissionIdIsDifferent() {
        CategoryTemplateModel other = new CategoryTemplateModel(categoryTemplate);
        other.setSubmissionId("differentId");

        assertThat(testCategoryTemplate.equals(other), is(false));
    }

    @Test
    void equalsReturnsFalseWhenCompanyNumberIsDifferent() {
        CategoryTemplateModel other = new CategoryTemplateModel(categoryTemplate);
        other.setCompanyNumber("differentNumber");

        assertThat(testCategoryTemplate.equals(other), is(false));
    }

    @Test
    void equalsReturnsFalseWhenCategoryTemplateListIsDifferent() {
        CategoryTemplateModel other = new CategoryTemplateModel(categoryTemplate);
        other.setCategoryTemplateList(Collections.singletonList(new CategoryTemplateApi(CT_02, "Category2", "INS", CT_02, null)));

        assertThat(testCategoryTemplate.equals(other), is(false));
    }
}