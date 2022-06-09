package uk.gov.companieshouse.efs.web.model;

import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Component;

@Component
//@SessionScope
public class ReviewSelectionModel {
    private String confirmed;

    /**
     * No argument Constructor.
     */
    public ReviewSelectionModel() {
        this.confirmed = "";
    }

    /**
     * Constructor sets the application id and required value from the {@link ReviewSelectionModel}.
     *
     * @param original the {@link ReviewSelectionModel}
     */
    public ReviewSelectionModel(final ReviewSelectionModel original) {
        this.confirmed = original.getConfirmed();
    }

    //    @NotBlank(message = "{removeDocument.error}")
    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final ReviewSelectionModel that = (ReviewSelectionModel) o;
        return Objects.equals(confirmed, that.confirmed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(confirmed);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("confirmed", confirmed).toString();
    }
}
