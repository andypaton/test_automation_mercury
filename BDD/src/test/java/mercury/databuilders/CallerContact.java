package mercury.databuilders;

import java.util.Random;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;

import mercury.database.models.CallerDetails;

public class CallerContact {
    private String forename;
    private String surname;
    private String name;
    private String department;
    private String telephone;
    private String extension;
    private String jobTitle;

    private String callerType;
    private String siteName;

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }


    public String getCallerType() {
        return callerType;
    }

    public void setCallerType(String callerType) {
        this.callerType = callerType;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }


    public static class Builder {
        private String forename;
        private String surname;
        private String name;
        private String department;
        private String telephone;
        private String extension;
        private String jobTitle;

        private String[] jobTitles = {"Administrator", "Bouncer", "Builder", "Caller", "City Facilities Management", "Closing manager", "Operator", "Owner", "Store Manager", "Supervisor", "Tech"};
        private String[] forenames = {"Sandor","Jaqen","Brienne","Drogon","Bronn","Stannis","Bran","Catelyn","Samwell","Daenerys","Jules","Ian","Liam","Daarios","Tyrion","Mario","Tiffany","Jon", "William", "David", "Micheal", "Robert", "Andrew","Jack", "Oliver", "Harry", "Charlie", "Jacob", "Alfie", "Oscar", "James", "Thomas", "Noah", "George", "Joshua", "Riley", "Max", "Ethan", "William", "Archie", "Leo", "Mason", "Lucas", "Logan", "Daniel", "Henry", "Joseph", "Jake", "Freddie", "Tyler", "Dylan", "Samuel", "Isaac", "Harrison", "Frankie", "David", "Michael", "Callum", "Luca", "Dexter", "Finlay", "Ben", "Leon", "Sam", "Cameron", "Jude", "Aaron", "Aiden", "Stanley", "Elliot", "Grant", "Jordan","Joffery","Petyr", "Arya","Sansa", "Colin", "Karol"};
        private String[] surnames = {"Mormont","Tarly","Dragon","Baelish","Baratheon","Seaworth","Gainstbaine","Greyjoy","Naharis","Stark","Targaryen","Lopez", "Lannister","Calls", "Stewart", "Snow", "Reid", "Murray", "Powell", "Palmer", "Holmes", "Rogers", "Stevens", "Walsh", "Hunter", "Thomson", "Matthews", "Ross", "Owen", "Mason", "Knight", "Kennedy", "Butler", "Saunders", "Cole", "Pearce", "Dean", "Foster", "Harvey", "Hudson", "Gibson", "Mills", "Berry", "Barnes", "Pearson", "Kaur", "Booth", "Dixon", "Grant", "Gordon", "Lane", "Harper", "Ali", "Hart", "Mcdonald", "Brooks", "Carr", "Macdonald", "Hamilton", "Johnston", "West", "Gill", "Dawson", "Armstrong", "Gardner", "Stone", "Andrews", "Williamson", "Barker", "George", "Fisher", "Cunningham", "Watts", "Webb", "Lawrence", "Bradley", "Wells", "Chambers", "Spencer", "Poole", "Atkinson", "Lawson", "Wolkanin"};
        private String[] departments = {"Building and Contruction","Customer Service","Frozen food", "Heldpesk", "Management", "No dept",	"Parts", "Refrigeration", "Store", "Store Manager", "Toy"};


        public Builder(){
            Random rand = new Random();
            this.surname=surnames[rand.nextInt(surnames.length)];
            this.forename=forenames[rand.nextInt(forenames.length)];
            this.name = forename.concat(" ").concat(surname);
            this.department = departments[rand.nextInt(departments.length)];
            this.telephone = DataGenerator.generatePhoneNumber();
            this.extension = String.format("%04d", rand.nextInt(9999));
            this.jobTitle = jobTitles[rand.nextInt(jobTitles.length)];
        }



        public CallerContact build() {
            return new CallerContact(this);
        }


    }

    private CallerContact(Builder builder){
        forename = builder.forename;
        surname = builder.surname;
        name = builder.name;
        department = builder.department;
        telephone = builder.telephone;
        extension = builder.extension;
        jobTitle = builder.jobTitle;

    }

    public CallerContact() {
    }

    public void copy(CallerContact callerContact) {
        BeanUtils.copyProperties(callerContact, this);
    }

    public void copy(CallerDetails callerDetails) {
        this.forename = "";
        this.surname = "";
        this.name = callerDetails.getName();
        this.department = callerDetails.getDepartment();
        this.telephone = callerDetails.getPhoneNumber();
        this.extension = callerDetails.getExtension();
        this.jobTitle = callerDetails.getJobRole();
        this.callerType = callerDetails.getCallerType();
        this.siteName = callerDetails.getSiteName();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("Name", name).append("department", department).append("telephone", telephone).append("extension", extension)
                .append("jobTitle", jobTitle).append("callerType", callerType).append("siteName", siteName).toString();
    }
}
