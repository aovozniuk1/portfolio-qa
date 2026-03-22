package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POJO representing a single user object from the jsonplaceholder API.
 * <p>
 * Maps to a user in {@code GET /users} or {@code GET /users/{id}}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserData {

    private int id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;
    private Address address;
    private Company company;

    public UserData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "UserData{id=" + id + ", name='" + name + "', username='" + username + "', email='" + email + "'}";
    }

    /**
     * Nested address object in the user response.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String street;
        private String suite;
        private String city;
        private String zipcode;
        private Geo geo;

        public Address() {
        }

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getSuite() { return suite; }
        public void setSuite(String suite) { this.suite = suite; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getZipcode() { return zipcode; }
        public void setZipcode(String zipcode) { this.zipcode = zipcode; }
        public Geo getGeo() { return geo; }
        public void setGeo(Geo geo) { this.geo = geo; }
    }

    /**
     * Nested geo object inside address.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geo {
        private String lat;
        private String lng;

        public Geo() {
        }

        public String getLat() { return lat; }
        public void setLat(String lat) { this.lat = lat; }
        public String getLng() { return lng; }
        public void setLng(String lng) { this.lng = lng; }
    }

    /**
     * Nested company object in the user response.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Company {
        private String name;
        private String catchPhrase;
        private String bs;

        public Company() {
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCatchPhrase() { return catchPhrase; }
        public void setCatchPhrase(String catchPhrase) { this.catchPhrase = catchPhrase; }
        public String getBs() { return bs; }
        public void setBs(String bs) { this.bs = bs; }
    }
}
