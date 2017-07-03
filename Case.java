/**
 * Created by Helen on 22/05/17.
 */
public class Case {
    int journeyCode;
    String holidayType;
    int price;
    int numPersons;
    String region;
    String transportation;
    int duration;
    String season;
    String accommodation;
    String hotel;

    public Case (){


    }

    @Override
    public String toString() {
        String result = "";
        if (this.journeyCode > 0)
            result += "JourneyCode: " + this.journeyCode + "\n";
        result += "HolidayType: " + this.holidayType + "\n";
        result += "Price: " + this.price + "\n";
        result += "NumberOfPersons: " + this.numPersons + "\n";
        result += "Region: " + this.region + "\n";
        result += "Transportation: " + this.transportation + "\n";
        result += "Duration: " + this.duration + "\n";
        result += "Season: " + this.season + "\n";
        result += "Accommodation: " + this.accommodation + "\n";
        result += "Hotel: " + this.hotel + "\n";

        return result;
    }
}
