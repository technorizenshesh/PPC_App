package com.codeinger.ppcapp.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberShipDetailModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("membershipGUID")
    @Expose
    private String membershipGUID;
    @SerializedName("addressLine1")
    @Expose
    private String addressLine1;
    @SerializedName("addressLine2")
    @Expose
    private String addressLine2;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("province")
    @Expose
    private String province;
    @SerializedName("postalCode")
    @Expose
    private String postalCode;
    @SerializedName("membershipEmails")
    @Expose
    private List<String> membershipEmails = null;
    @SerializedName("membershipPhoneNumbers")
    @Expose
    private List<String> membershipPhoneNumbers = null;
    @SerializedName("joinedDate")
    @Expose
    private String joinedDate;
    @SerializedName("expiryDate")
    @Expose
    private String expiryDate;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("membershipPlan")
    @Expose
    private Object membershipPlan;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getMembershipGUID() {
        return membershipGUID;
    }

    public void setMembershipGUID(String membershipGUID) {
        this.membershipGUID = membershipGUID;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public List<String> getMembershipEmails() {
        return membershipEmails;
    }

    public void setMembershipEmails(List<String> membershipEmails) {
        this.membershipEmails = membershipEmails;
    }

    public List<String> getMembershipPhoneNumbers() {
        return membershipPhoneNumbers;
    }

    public void setMembershipPhoneNumbers(List<String> membershipPhoneNumbers) {
        this.membershipPhoneNumbers = membershipPhoneNumbers;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Object getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(Object membershipPlan) {
        this.membershipPlan = membershipPlan;
    }

}