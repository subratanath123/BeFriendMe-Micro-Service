package net.befriendme.entity.business;

import net.befriendme.entity.user.Address;
import net.befriendme.entity.user.User;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document
@Data
public class Business implements Serializable {

    @Id
    private String id;

    @DBRef
    private User owner;

    private String businessLogoUrl;
    private String coverPhotoUrl;
    private String businessName;
    private String businessStartingYear;
    private String businessCategoryId;
    private String businessEmail;
    private String businessWebsite;
    private String businessType;
    private String businessNumber;
    private String aboutBusiness;
    private String businessLicenseNumber;
    private String dialCode;
    private String isoCode;
    private Address address;

    @DBRef
    private List<SubscriptionPackage> subscriptionPackageList;

    @DBRef
    private List<User> userLikedList;

    public Business() {
    }
}
