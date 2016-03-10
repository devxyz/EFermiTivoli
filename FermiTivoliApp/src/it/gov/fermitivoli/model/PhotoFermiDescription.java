package it.gov.fermitivoli.model;

/**
 * Descrizione delle foto
 */
public class PhotoFermiDescription {
    public final String category;
    public final String description;
    public final String imageUrl;

    public PhotoFermiDescription(String category, String description, String imageUrl) {
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

}
