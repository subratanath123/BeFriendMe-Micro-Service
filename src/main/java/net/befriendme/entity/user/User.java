package net.befriendme.entity.user;

import net.befriendme.entity.business.Business;
import net.befriendme.entity.idp.IdpProvider;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.GeoIndexed;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class User implements Serializable {

    public static final String COLLECTION_NAME = "user";

    @Id
    private String id;

    @NotEmpty
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "{invalid.email}")
    private String email;
    private String phone;
    private boolean isPrivateProfile;
    private List<IdpProvider> idpProviderList;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String displayName;

    @NotEmpty
    private String gender;
    private String designation;
    private String organization;
    private String aboutUser;
    private String coverPhotoUrl;
    private String profilePictureUrl;

    @NotEmpty
    private int age;

    private LocalDate birthDate;

    private boolean hasBusiness;

    @NotEmpty
    private List<Address> addressList;

    @DBRef
    private List<Interest> interestList;

    @DBRef
    private List<Business> businessList;
    private List<SecurityQuestion> securityQuestionList;

    private Visibility visibility;
    private List<User> friendList;
    private List<Group> groupList;
    private List<Page> pageList;
    private List<User> privacyList;

    @GeoIndexed
    private Point currentLocation;

    public User(String id) {
        this();
        this.id = id;
    }

    public User() {
        securityQuestionList = new ArrayList<>();
        businessList = new ArrayList<>();
        interestList = new ArrayList<>();
        addressList = new ArrayList<>();
        idpProviderList = new ArrayList<>();
    }
}