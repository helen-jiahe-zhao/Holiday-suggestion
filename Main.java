import java.io.*;
import java.util.*;

import static java.lang.System.*;

public class Main {

    public static ArrayList<Case> cases = new ArrayList<>();
    public static HashMap<String, Integer> weights = new HashMap<>();

    //discrete inputs
    public static HashSet<String> holidayType = new HashSet<>();
    public static HashSet<String> region = new HashSet<>();
    public static HashSet<String> accom = new HashSet<>();
    public static HashSet<String> transportation = new HashSet<>();
    public static HashSet<String> seasons = new HashSet<>();
    public static HashMap<String, String> hotel = new HashMap<>();

    //instance variables
    public static boolean regionByFeature = false;

    public static int easternEuropeCode = 1;
    public static int westernEuropeCode = 2;
    public static int northernEuropeCode = 3;
    public static int middleEastCode = 4;
    public static int mountainsCode = 5;
    public static int forestCode = 6;
    public static int lakeCode = 7;
    public static int coastCode = 8;
    public static int seaCode = 9;
    public static int islandCode = 10;
    public static int historicCode = 11;
    public static int otherCode = 12;

    public static int numTopCases = 5;

    //region sets
    public static HashMap<Integer, HashSet<String>> regionSet  = new HashMap<>();

    /*
    PRIMARY FUNCTIONS
     */
    private static void constructCases(){
        try {
            String filename = "travel.csv";
            Scanner s = new Scanner(new File(filename));
            s.useDelimiter(",");
            Case c;
            while(s.hasNext()){
                //first item of the case

                while (!(s.next().equals("defcase")));
                c = new Case();

                //journey number
                int num = Integer.parseInt(s.next().trim());
                c.journeyCode = num;

                //skip ahead until holiday type;
                while (!(s.next().equals("HolidayType"))) ;
                c.holidayType = s.next().trim();
                holidayType.add(c.holidayType.toLowerCase());

                //skip ahead until price
                while (!(s.next().equals("Price"))) ;
                c.price = Integer.parseInt(s.next().trim());

                //skip ahead until num persons
                while (!(s.next().equals("NumberOfPersons"))) ;
                c.numPersons = Integer.parseInt(s.next().trim());

                //skip ahead until region
                while (!(s.next().equals("Region"))) ;
                c.region = s.next().trim();
                region.add(c.region.toLowerCase());

                //skip ahead until transportation
                while (!(s.next().equals("Transportation"))) ;
                c.transportation = s.next().trim();
                transportation.add(c.transportation.toLowerCase());

                //skip ahead until duration
                while (!(s.next().equals("Duration"))) ;
                c.duration = Integer.parseInt(s.next().trim());


                //skip ahead until season
                while (!(s.next().equals("Season"))) ;
                c.season = s.next().trim();
                seasons.add(c.season.toLowerCase());

                //skip ahead until accommodation
                while (!(s.next().equals("Accommodation"))) ;
                c.accommodation = s.next().trim();
                accom.add(c.accommodation.toLowerCase());


                //skip ahead until hotel
                while (!(s.next().equals("Hotel"))) ;
                c.hotel = s.next().trim();
                hotel.put(c.hotel, c.region.toLowerCase());


                cases.add(c);
            }


            s.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    private static void constructWeights() {
        int holidayTypeWeight = 3;
        int priceWeight = 8;
        int numPersonsWeight = 6;
        int regionWeight = 5;
        int transportationWeight = 3;
        int durationWeight = 7;
        int seasonWeight = 4;
        int accommodationWeight = 4;
        int hotelWeight = 2;

        weights.put("HolidayType", holidayTypeWeight);
        weights.put("Price", priceWeight);
        weights.put("NumberOfPersons", numPersonsWeight);
        weights.put("Region", regionWeight);
        weights.put("Transportation", transportationWeight);
        weights.put("Duration", durationWeight);
        weights.put("Season", seasonWeight);
        weights.put("Accommodation", accommodationWeight);
        weights.put("Hotel", hotelWeight);

    }
    private static Case obtainCase() {
        Case userCase = new Case();
        try {
            System.out.println("-----");

            System.out.println("Press \"Enter\" to skip any of the following options:");
            System.out.println("-----");

            System.out.println("What type of holiday would you like?");
            System.out.println("(Options: Active, Language, Education, Recreation, Skiing, Bathing, City, or Wandering)");
            Scanner s = new Scanner(System.in);
            userCase.holidayType = s.nextLine().trim();

            boolean inputValid = false;
            String item = "";
            System.out.println("What is your budget?");
            while (!inputValid) {
                System.out.println("(Please enter a numerical value:)");
                item = s.nextLine().trim();
                if (item.length() == 0)
                    inputValid = true;
                else if (item.matches("-?\\d+(\\.\\d+)?"))
                    inputValid = true;
            }

            int price = 0;
            if (item.length() != 0) {
                price = Integer.parseInt(item);
            }
            userCase.price = price;



            inputValid = false;
            item = "";
            System.out.println("How many people are travelling?");
            while (!inputValid) {
                System.out.println("(Please enter a numerical value:)");
                item = s.nextLine().trim();
                if (item.length() == 0)
                    inputValid = true;
                else if (item.matches("-?\\d+(\\.\\d+)?"))
                    inputValid = true;
            }


            int numPeople = 0;
            if (item.length() != 0) {
                numPeople = Integer.parseInt(item);
            }
            userCase.numPersons = numPeople;

            //finding region
            String input = obtainRegion(s);
            userCase.region = input;


            System.out.println("How do you want to get there?");
            System.out.println("(Options: Plane, Car, Coach, Train)");
            userCase.transportation = s.nextLine().trim();
            

            System.out.println("How long do you want to go for?:");
            System.out.println("(Please enter a numerical value:)");
            item = s.nextLine().trim();
            int days = 0;
            if (item.length() != 0) {
                days = Integer.parseInt(item);
            }
            userCase.duration = days;

            System.out.println("Which month did you want to travel in?");
            System.out.println("(Please type in english: January to December)");
            userCase.season = s.nextLine().trim();


            System.out.println("What type of accommodation would you like?");
            System.out.println("(Options: HolidayFlat, or OneStar to FiveStars");
            userCase.accommodation = s.nextLine().trim();

            System.out.println("Which hotel do you want to stay at?");
            System.out.println("(Here are the options corresponding to your destination: )");
            printRelevantHotels(userCase.region);
            userCase.hotel = s.nextLine().trim();


            System.out.println();
            System.out.println("-----");

            System.out.println("Here is the trip that you have specified:");

            System.out.println(userCase.toString());

            inputValid = false;
            item = "";
            System.out.println("How many results would you like to see?");
            while (!inputValid) {
                System.out.println("(Please enter a numerical value. Default = 5)");
                item = s.nextLine().trim();
                if (item.length() == 0)
                    inputValid = true;
                else if (item.matches("-?\\d+(\\.\\d+)?"))
                    inputValid = true;
            }
            if (item.length() > 0)
                numTopCases = Integer.parseInt(item);

        } catch (Exception NumberFormatException){
            System.out.println("Please input the correct format!");
            exit(0);
        }

        return userCase;

    }
    private static String obtainRegion(Scanner s) {
        boolean inputValid = false;

        String item = "";

        System.out.println("Where do you want to go?");
        while (!inputValid) {
            System.out.println("Please enter a number corresponding to one of the following:");
            System.out.println("1: choose by country");
            System.out.println("2: choose by features");
            System.out.println("3: show all regions");


            item = s.nextLine().trim();
            if (item.length() == 0)
                //return if user doesn't want to input anything
                return "";
            else if (item.matches("-?[1-3]?"))
                inputValid = true;
        }

        int chooseType = Integer.parseInt(item);


        //choose region by country
        if (chooseType == 1) {
            inputValid = false;
            item = "";
            while (!inputValid) {
                System.out.println("Please enter a number corresponding to one of the following:");
                System.out.println(easternEuropeCode + ": Eastern Europe");
                System.out.println(westernEuropeCode + ": Western Europe");
                System.out.println(northernEuropeCode + ": Northern Europe");
                System.out.println(middleEastCode + ": Middle East");

                item = s.nextLine().trim();
                if (item.length() == 0)
                    //return if user doesn't want to input anything
                    return "";
                else if (item.matches("-?[1-4]?"))
                    inputValid = true;
            }
            int regionCode = Integer.parseInt(item);

            HashSet<String> relevantRegions = regionSet.get(regionCode);
            Object[] relevantRegionsArray = relevantRegions.toArray();
            Arrays.sort(relevantRegionsArray);

            System.out.println("Here are your options:");
            for (Object i : relevantRegionsArray) {
                System.out.println(i);
            }
            item = s.nextLine().trim();


        } else if (chooseType == 2) {
            regionByFeature = true;
            //region by features
            inputValid = false;
            item = "";
            while (!inputValid) {
                System.out.println("Please enter a number corresponding to one of the following:");
                System.out.println(mountainsCode + ": Mountains");
                System.out.println(forestCode + ": Forests");
                System.out.println(lakeCode + ": Lakes");
                System.out.println(coastCode + ": Coasts");
                System.out.println(seaCode + ": Sea");
                System.out.println(islandCode + ": Islands");
                System.out.println(historicCode + ": Historic");
                System.out.println(otherCode + ": Other");

                item = s.nextLine().trim();
                if (item.length() == 0)
                    //return if user doesn't want to input anything
                    return "";
                else if (item.matches("-?\\d+(\\.\\d+)?")) {
                    //if the value lies within the range for these codes
                    if (Integer.parseInt(item) < 13 && Integer.parseInt(item) > 4)
                        inputValid = true;
                }
            }
            int regionCode = Integer.parseInt(item);

            HashSet<String> relevantRegions = regionSet.get(regionCode);
            Object[] relevantRegionsArray = relevantRegions.toArray();
            Arrays.sort(relevantRegionsArray);

            System.out.println("Here are your options:");
            for (Object i : relevantRegionsArray) {
                System.out.println(i);
            }
            item = s.nextLine().trim();




        } else {
            //print all regions
            Object[] r = region.toArray();
            Arrays.sort(r);

            System.out.println("Here are all regions:");
            for (Object re : r) {
                System.out.println(re);
            }
            item = s.nextLine().trim();

        }


        return item;
    }
    private static void printRelevantHotels(String region) {
        Iterator<Map.Entry<String,String>> it = hotel.entrySet().iterator();


        while (it.hasNext()) {
            Map.Entry<String,String> entry = it.next();
            String currentRegion = entry.getValue().toLowerCase();
            String currentHotel = entry.getKey();


            //print if they have same region, or if region is not specified by the user
            if (currentRegion.equals(region.toLowerCase()) || region.length() == 0) {
                System.out.println(currentHotel);
            }
        }

        System.out.println();
    }
    private static ArrayList<Map.Entry<Double, Case>> retrieveCases(Case userCase) {
        TreeMap<Double, Case> simResults = new TreeMap<>();
        ArrayList<Map.Entry<Double, Case>> results = new ArrayList<>();

        double increment = 0.000001;
        for (Case c : cases) {
            double sim = calculateSimilarity(c, userCase);
            //so that all the sim values will be slightly different
            sim += increment;
            increment += 0.000001;
            simResults.put(sim, c);
        }

        for (int i = 0; i < numTopCases; i++) {
            Map.Entry<Double, Case> item = simResults.pollLastEntry();
            results.add(item);
        }

        return results;
    }
    private static double calculateSimilarity(Case c, Case userCase) {
        double totalSim;

        double holidayTypeSim = holidayTypeSimilarity(c, userCase);
        double priceSim = priceSimilarity(c, userCase);
        double numPersonsSim = numPersonsSimilarity(c, userCase);
        double regionSim = regionSimilarity(c, userCase);
        double transportationSim = transportationSimilarity(c, userCase);
        double durationSim = durationSimilarity(c, userCase);
        double seasonSim = seasonSimilarity(c, userCase);
        double accommodationSim = accommodationSimilarity(c, userCase);
        double hotelSim = hotelSimilarity(c, userCase);

        int totalWeight = 0;
        int holidayTypeWeight = weights.get("HolidayType");
        int priceWeight = weights.get("Price");
        int numPersonsWeight = weights.get("NumberOfPersons");
        int regionWeight = weights.get("Region");
        int transportationWeight = weights.get("Transportation");
        int durationWeight = weights.get("Duration");
        int seasonWeight = weights.get("Season");
        int accommodationWeight = weights.get("Accommodation");
        int hotelWeight = weights.get("Hotel");

        totalWeight += holidayTypeWeight;
        totalWeight += priceWeight;
        totalWeight += numPersonsWeight;
        totalWeight += regionWeight;
        totalWeight += transportationWeight;
        totalWeight += durationWeight;
        totalWeight += seasonWeight;
        totalWeight += accommodationWeight;
        totalWeight += hotelWeight;

        totalSim = (holidayTypeSim * holidayTypeWeight + priceSim * priceWeight + numPersonsSim * numPersonsWeight + regionSim*regionWeight
                + transportationSim * transportationWeight + durationSim * durationWeight + seasonSim * seasonWeight
                + accommodationSim * accommodationWeight + hotelSim * hotelWeight) / totalWeight;

        return totalSim;
    }
    private static void returnResults(ArrayList<Map.Entry<Double, Case>> topCases) {
        System.out.println();
        System.out.println("-----");
        System.out.println("Here are the top " + numTopCases + " cases: ");
        for (Map.Entry c : topCases) {
            String simString = c.getKey() + "";
            double simDouble = Double.parseDouble(simString);
            System.out.printf("Similarity: %.2f \n", simDouble);

            System.out.println(c.getValue().toString());
        }
    }


    /*
    INDIVIDUAL SIMILARITY FUNCTIONS
     */

    private static double holidayTypeSimilarity(Case c, Case userCase) {
        double similarity = 0.0;

        //return 0 similarity if input not valid
        if (userCase.holidayType.length() == 0)
            return similarity;

        String caseType = c.holidayType.toLowerCase();
        String userType = userCase.holidayType.toLowerCase();

        //use word edit if typo
        if (!holidayType.contains(userCase.holidayType.toLowerCase())){
            String mostSimilarWord =  findMostSimilarInput(userType, holidayType);
            userType = mostSimilarWord;
        }

        HashMap<String, Integer> typeKeys = constructTypeHashMap();
        double[][] matrix = constructTypeMatrix();

        int caseNumericTransportation =  typeKeys.get(caseType);
        int userNumericTransportation =  typeKeys.get(userType);

        similarity = matrix[userNumericTransportation][caseNumericTransportation];

        return similarity;

    }
    private static double priceSimilarity(Case c, Case userCase) {
        double similarity = 0.0;

        double casePrice = c.price;
        double userPrice = userCase.price;



        //normalise by number of people or night, if they exist
        if (userCase.numPersons > 0) {
            casePrice = casePrice / c.numPersons;
            userPrice = userPrice / userCase.numPersons;

        }

        if (userCase.duration > 0) {
            casePrice = casePrice / c.duration;
            userPrice = userPrice / userCase.duration;
        }

        //return similarity of 0 if the case price is over the budget
        if (casePrice > userPrice) {
            return similarity;
        }

        double diff = (double) Math.abs(casePrice - userPrice)/Math.max(casePrice, userPrice);
        similarity = 1 - diff;
        if (similarity < 0) similarity = 0;

        System.out.println("user price was " + userPrice + " and case price was " + casePrice + "and sim was "+ similarity);
        return similarity;
    }
    private static double numPersonsSimilarity(Case c, Case userCase) {
        double similarity = 0.0;

        int caseNumPersons = c.numPersons;
        int userNumPersons = userCase.numPersons;

        if (userNumPersons < 0) {
            return similarity;
        }

        if (caseNumPersons < userNumPersons) {
            return similarity;
        }

        double diff = (double) Math.abs(caseNumPersons - userNumPersons)/ Math.max(caseNumPersons, userNumPersons);
        similarity = 1 - diff;
        if (similarity < 0) similarity = 0;


        return similarity;
    }
    private static double regionSimilarity(Case c, Case userCase) {
        double similarity = 0.0;

        //return 0 similarity if input not valid
        if (userCase.region.length() == 0)
            return similarity;

        String caseRegion = c.region.toLowerCase();
        String userRegion = userCase.region.toLowerCase().replaceAll("\\s+", "");

        //use word edit if typo
        if (!region.contains(userCase.region.toLowerCase())){
            String mostSimilarWord =  findMostSimilarInput(userRegion, region);
            userRegion = mostSimilarWord;
        }

        if (caseRegion.equals(userRegion)) return 1.0;


        //use similarity measures by country
        boolean sameSet = false;

        if (regionByFeature){
            //use similarity measures by region
            //get the hashsets by country
            for (int i = 25; i < 33; i++) {
                HashSet<String> set = regionSet.get(i);
                if (set.contains(caseRegion) && set.contains(userRegion)) {
                    //if in the same feature set, sim = 0.7
                    similarity = 0.7;
                    sameSet = true;
                    break;
                }
            }
            if (!sameSet)
                similarity = 0.2;
        } else {
            //get the hashsets by country
            for (int i = 21; i < 25; i++) {
                HashSet<String> set = regionSet.get(i);
                //if in the same location, sim = 0.6
                if (set.contains(caseRegion) && set.contains(userRegion)) {
                    similarity = 0.6;
                    sameSet = true;
                    break;
                }
            }
            if (!sameSet)
                similarity = 0.2;

        }

        return similarity;
    }
    private static double transportationSimilarity(Case c, Case userCase) {
        double similarity = 0.0;


        //return 0 similarity if input not valid
        if (userCase.transportation.length() == 0)
            return similarity;

        String caseTransportation = c.transportation.toLowerCase();
        String userTransportation = userCase.transportation.toLowerCase();

        //use word edit if typo
        if (!transportation.contains(userCase.transportation.toLowerCase())){
            String mostSimilarWord =  findMostSimilarInput(userTransportation, transportation);
            userTransportation = mostSimilarWord;
        }



        HashMap<String, Integer> transportKeys = constructTransports();
        double[][] matrix = constructTransportMatrix();

        int caseNumericTransportation =  transportKeys.get(caseTransportation);
        int userNumericTransportation =  transportKeys.get(userTransportation);

        similarity = matrix[userNumericTransportation][caseNumericTransportation];

        return similarity;
    }
    private static double durationSimilarity(Case c, Case userCase) {
        double similarity = 0.0;

        int caseDuration = c.duration;
        int userDuration = userCase.duration;

        if (userDuration < 0) {
            return similarity;
        }

        if (caseDuration > userDuration) {
            return similarity;
        }

        double diff = (double) Math.abs(caseDuration - userDuration)/Math.max(caseDuration, userDuration);

        similarity = 1 -  diff;
        if (similarity < 0) similarity = 0;
        return similarity;
    }
    private static double seasonSimilarity(Case c, Case userCase) {
        double similarity = 0.0;

        //return 0 similarity if input not valid
        if (userCase.season.length() == 0)
            return similarity;

        //true if a valid season

        String caseMonth = c.season.toLowerCase();
        String userMonth = userCase.season.toLowerCase();

        //use word edit if typo
        if (!seasons.contains(userCase.season.toLowerCase())) {
            String mostSimilarWord =  findMostSimilarInput(userMonth, seasons);
            userMonth = mostSimilarWord;
        }

        int numericCaseMonth = convertToNumericMonths(caseMonth);
        int numericUserMonth = convertToNumericMonths(userMonth);

        int monthDistance = Math.abs(numericCaseMonth - numericUserMonth);

        HashMap<Integer, Integer> seasons = constructSeasons();

        if (numericCaseMonth == numericUserMonth) {
            //if same month, return 1
            similarity = 1.0;
            return similarity;
        }

        if (monthDistance == 1 || monthDistance == 11) {
            //only 1 month apart
            similarity = 0.8;
            return similarity;
        }

        //determine by season
        int caseSeason = seasons.get(numericCaseMonth);
        int userSeason = seasons.get(numericUserMonth);
        int seasonDistance = Math.abs(caseSeason - userSeason);

        if (caseSeason == userSeason) {
            //same season
            similarity = 0.8;
        } else if ((caseSeason == 1 && userSeason == 3) || (caseSeason == 3 && userSeason == 1)) {
            //spring and fall
            similarity = 0.7;
        } else if (seasonDistance == 1 ||seasonDistance == 3) {
            //neighbouring seasons
            similarity = 0.5;
        }

        return similarity;
    }
    private static double accommodationSimilarity(Case c, Case userCase) {
        double similarity = 0.0;

        //return 0 similarity if input not valid
        if (userCase.accommodation.length() == 0)
            return similarity;

        String caseAccom = c.accommodation.toLowerCase();
        String userAccom = userCase.accommodation.toLowerCase();

        //use word edit if typo
        if (!accom.contains(userCase.accommodation.toLowerCase())) {
            String mostSimilarWord =  findMostSimilarInput(userAccom, accom);
            userAccom = mostSimilarWord;
        }

        if (userAccom.equals(caseAccom)) {
            similarity = 1.0;
            return similarity;
        } else if (!(userAccom.equals("holidayflat") || caseAccom.equals("holidayflat"))) {
            //continue when neither are holidayflat
            int caseStars = convertToNumericStars(caseAccom);
            int userStars = convertToNumericStars(userAccom);

            //continue if case has greater star
            if (caseStars > userStars) {
                int distance = caseStars - userStars;
                switch (distance) {
                    case 1: similarity = 0.8; break;
                    case 2: similarity = 0.6; break;
                    case 3: similarity = 0.4; break;
                    case 4: similarity = 0.2; break;
                }
            }
        }

        return similarity;
    }
    private static double hotelSimilarity(Case c, Case userCase) {
        double similarity = 0.0;


        if (userCase.hotel.length() == 0)
            return similarity;


        String caseHotel = c.hotel.toLowerCase();
        String userHotel = userCase.hotel.toLowerCase();

        similarity = getSentenceSimilarity(caseHotel, userHotel);

        if (similarity < 0) similarity = 0;

        return similarity;
    }


    /*
    FUNCTIONS FOR CONSTRUCTION
     */

    private static HashMap<String, Integer> constructTypeHashMap() {
        HashMap<String, Integer> result = new HashMap<>();
        result.put("city", 0);
        result.put("education", 1);
        result.put("language", 2);
        result.put("recreation", 3);
        result.put("bathing", 4);
        result.put("wandering", 5);
        result.put("active", 6);
        result.put("skiing", 7);

        return result;
    }
    private static HashMap<Integer, Integer> constructSeasons() {
        HashMap<Integer, Integer> seasons = new HashMap<>();
        for (int i = 1; i < 13; i++) {
            //i is the month
            //spring
            if (i == 3 || i == 4 || i == 5) {
                seasons.put(i, 1);
            }
            //summer
            if (i == 6 || i == 7 || i == 8) {
                seasons.put(i, 2);
            }
            //autumn
            if (i == 9 || i == 10 || i == 11) {
                seasons.put(i, 3);
            }
            //winter
            if (i == 12 || i == 1 || i == 2) {
                seasons.put(i, 4);
            }
        }

        return seasons;

    }
    private static HashMap<String, Integer> constructTransports() {
        HashMap<String,Integer> map = new HashMap<>();
        map.put("train", 0);
        map.put("car", 1);
        map.put("plane", 2);
        map.put("coach", 3);

        return map;
    }
    private static void constructRegions() {
        //construct all hashsets
        HashSet<String> easternEurope = new HashSet<>();
        HashSet<String> westernEurope = new HashSet<>();
        HashSet<String> northernEurope = new HashSet<>();
        HashSet<String> middleEast = new HashSet<>();
        HashSet<String> mountains = new HashSet<>();
        HashSet<String> forest = new HashSet<>();
        HashSet<String> lake = new HashSet<>();
        HashSet<String> coast = new HashSet<>();
        HashSet<String> sea = new HashSet<>();
        HashSet<String> island = new HashSet<>();
        HashSet<String> historic = new HashSet<>();
        HashSet<String> other = new HashSet<>();

        //new set of hashsets for lowercase
        HashSet<String> easternEurope2 = new HashSet<>();
        HashSet<String> westernEurope2 = new HashSet<>();
        HashSet<String> northernEurope2 = new HashSet<>();
        HashSet<String> middleEast2 = new HashSet<>();
        HashSet<String> mountains2 = new HashSet<>();
        HashSet<String> forest2 = new HashSet<>();
        HashSet<String> lake2 = new HashSet<>();
        HashSet<String> coast2 = new HashSet<>();
        HashSet<String> sea2 = new HashSet<>();
        HashSet<String> island2 = new HashSet<>();
        HashSet<String> historic2 = new HashSet<>();
        HashSet<String> other2 = new HashSet<>();


        //put things in easternEurope
        String eastString = "Slowakei\n" +
                "Czechia\n" +
                "GiantMountains\n" +
                "Balaton\n" +
                "Styria\n" +
                "SalzbergerLand\n" +
                "Tyrol\n" +
                "Carinthia\n" +
                "Crete\n" +
                "Rhodes\n" +
                "Corfu\n" +
                "Attica\n" +
                "Chalkidiki\n" +
                "Malta\n" +
                "Poland\n" +
                "Bulgaria" ;


        String westString = "LakeGarda\n" +
                "AdriaticSea\n" +
                "Fano\n" +
                "GranCanaria\n" +
                "Teneriffe\n" +
                "Fuerteventura\n" +
                "CostaBlanca\n" +
                "Ibiza\n" +
                "Mallorca\n" +
                "CostaBrava\n" +
                "France\n" +
                "Alps\n" +
                "Corsica\n" +
                "Brittany\n" +
                "Riviera\n" +
                "CotedAzur\n" +
                "Normandy\n" +
                "Harz\n" +
                "Bavaria\n" +
                "ErzGebirge\n" +
                "BlackForest\n" +
                "Thuringia\n" +
                "Allgaeu\n" +
                "Algarve\n" +
                "Madeira\n" +
                "Wales\n" +
                "England\n" +
                "Scotland\n" +
                "Belgium \n" +
                "Ireland\n" +
                "Holland";

        String northString = "Denmark\n" +
                "Bornholm\n" +
                "NorthSea\n" +
                "Lolland\n" +
                "Sweden";

        String middleString = "Egypt\n" +
                "Cairo\n" +
                "TurkishRiviera\n" +
                "TurkishAegeanSea\n" +
                "Tunisia";
        String mountainString = "Harz\n" +
                "Alps\n" +
                "ErzGebirge\n" +
                "GiantMountains\n" +
                "Styria\n" +
                "Tyrol\n" +
                "Allgaeu";

        String forestString = "BlackForest\n" +
                "Thuringia\n" +
                "Styria";

        String lakeString = "LakeGarda\n" +
                "Balaton\n" +
                "Carinthia";

        String coastString = "Algarve\n" +
                "CostaBlanca\n" +
                "Riviera\n" +
                "CotedAzur\n" +
                "TurkishRiviera\n" +
                "CostaBrava\n" +
                "Normandy\n" +
                "Fano\n" +
                "Chalkidiki";

        String seaString = "NorthSea\n" +
                "AdriaticSea\n" +
                "Atlantic\n" +
                "BalticSea\n" +
                "TurkishAegeanSea";

        String islandString = "Bornholm\n" +
                "GranCanaria\n" +
                "Teneriffe\n" +
                "Fuerteventura\n" +
                "Corsica\n" +
                "Crete\n" +
                "Rhodes\n" +
                "Ibiza\n" +
                "Corfu\n" +
                "Mallorca\n" +
                "Madeira\n" +
                "Malta\n" +
                "Lolland";

        String historicString = "Bavaria\n" +
                "SalzbergerLand\n" +
                "Slowakei\n" +
                "Czechia\n" +
                "Egypt\n" +
                "Brittany\n" +
                "Bulgaria\n" +
                "France\n" +
                "Attica\n" +
                "Cairo";

        String otherString = "Wales\n" +
                "England\n" +
                "Belgium \n" +
                "Scotland\n" +
                "Sweden\n" +
                "Ireland\n" +
                "Poland\n" +
                "Tunisia\n" +
                "Holland\n" +
                "Denmark";

        String[] eastRegions = eastString.split("\n");
        for (String i : eastRegions) {
            easternEurope.add(i);
            easternEurope2.add(i.toLowerCase());
        }

        String[] westRegions = westString.split("\n");
        for (String i : westRegions) {
            westernEurope.add(i);
            westernEurope2.add(i.toLowerCase());
        }

        String[] northRegions = northString.split("\n");
        for (String i : northRegions) {
            northernEurope.add(i);
            northernEurope2.add(i.toLowerCase());
        }

        String[] middleRegions = middleString.split("\n");
        for (String i : middleRegions) {
            middleEast.add(i);
            middleEast2.add(i.toLowerCase());
        }

        String[] mountainRegions = mountainString.split("\n");
        for (String i : mountainRegions) {
            mountains.add(i);
            mountains2.add(i.toLowerCase());
        }

        String[] forestRegions = forestString.split("\n");
        for (String i : forestRegions) {
            forest.add(i);
            forest2.add(i.toLowerCase());
        }


        String[] lakeRegions = lakeString.split("\n");
        for (String i : lakeRegions) {
            lake.add(i);
            lake2.add(i.toLowerCase());
        }



        String[] coastRegions = coastString.split("\n");
        for (String i : coastRegions) {
            coast.add(i);
            coast2.add(i.toLowerCase());
        }

        String[] seaRegions = seaString.split("\n");
        for (String i : seaRegions) {
            sea.add(i);
            sea2.add(i.toLowerCase());
        }

        String[] islandRegions = islandString.split("\n");
        for (String i : islandRegions) {
            island.add(i);
            island2.add(i.toLowerCase());
        }

        String[] historicRegions = historicString.split("\n");
        for (String i : historicRegions) {
            historic.add(i);
            historic2.add(i.toLowerCase());
        }


        String[] otherRegions = otherString.split("\n");
        for (String i : otherRegions) {
            other.add(i);
            other2.add(i.toLowerCase());
        }


        //put all hashsets into the region set
        regionSet.put(easternEuropeCode,easternEurope);
        regionSet.put(westernEuropeCode,westernEurope);
        regionSet.put(northernEuropeCode,northernEurope);
        regionSet.put(middleEastCode,middleEast);
        regionSet.put(mountainsCode,mountains);
        regionSet.put(forestCode,forest);
        regionSet.put(lakeCode,lake);
        regionSet.put(coastCode,coast);
        regionSet.put(seaCode,sea);
        regionSet.put(islandCode,island);
        regionSet.put(historicCode,historic);
        regionSet.put(otherCode,other);


        regionSet.put(easternEuropeCode+20,easternEurope2);
        regionSet.put(westernEuropeCode+20,westernEurope2);
        regionSet.put(northernEuropeCode+20,northernEurope2);
        regionSet.put(middleEastCode+20,middleEast2);
        regionSet.put(mountainsCode+20,mountains2);
        regionSet.put(forestCode+20,forest2);
        regionSet.put(lakeCode+20,lake2);
        regionSet.put(coastCode+20,coast2);
        regionSet.put(seaCode+20,sea2);
        regionSet.put(islandCode+20,island2);
        regionSet.put(historicCode+20,historic2);
        regionSet.put(otherCode+20,other2);

    }
    private static double[][] constructTypeMatrix() {
        double[][] matrix = new double[8][8];
        matrix[0] = new double[] {1, 0.7, 0.7, 0.2, 0.2, 0.2, 0.2, 0.2};
        matrix[1] = new double[] {0.7, 1, 0.7, 0.2, 0.2, 0.2, 0.2, 0.2};
        matrix[2] = new double[] {0.7, 0.7, 1, 0.2, 0.2, 0.2, 0.2, 0.2};
        matrix[3] = new double[] {0.2, 0.2, 0.2, 1, 0.6, 0.6, 0.6, 0.6};
        matrix[4] = new double[] {0.2, 0.2, 0.2, 0.6, 1, 0.6, 0.6, 0.6};
        matrix[5] = new double[] {0.2, 0.2, 0.2, 0.6, 0.6, 1, 0.6, 0.6};
        matrix[6] = new double[] {0.2, 0.2, 0.2, 0.6, 0.6, 0.6, 1, 0.7};
        matrix[7] = new double[] {0.2, 0.2, 0.2, 0.6, 0.6, 0.6, 0.7, 1};

        return matrix;
    }
    private static double[][] constructTransportMatrix() {
        double[][] mat = new double[4][4];
        mat[0] = new double[] {1, 0.3, 0.5, 0.8};
        mat[1] = new double[] {0.7, 1, 0.1, 0.7};
        mat[2] = new double[] {0.5, 0.1, 1, 0.3};
        mat[3] = new double[] {0.8, 0.3, 0.3, 1};

        return mat;
    }

    /*
    FUNCTIONS FOR CONVERSION AND CALCULATION
     */
    private static int convertToNumericStars(String accom) {
        int result = 0;
        switch (accom) {
            case "onestar": result = 1; break;
            case "twostars": result = 2; break;
            case "threestars": result = 3; break;
            case "fourstars": result = 4; break;
            case "fivestars": result = 5; break;
        }

        return result;
    }
    private static int convertToNumericMonths(String month) {
        int result = 0;
        switch (month) {
            case "january": result = 1; break;
            case "february": result = 2; break;
            case "march": result = 3; break;
            case "april": result = 4; break;
            case "may": result = 5; break;
            case "june": result = 6; break;
            case "july": result = 7; break;
            case "august": result = 8; break;
            case "september": result = 9; break;
            case "october": result = 10; break;
            case "november": result = 11; break;
            case "december": result = 12; break;

        }

        return result;
    }
    private static int getWordDistance(String s1, String s2) {
        int distance = 0;

        char[] chars1 = s1.toCharArray();
        char[] chars2 = s2.toCharArray();

        int[][] mat = new int[chars1.length+1][chars2.length+1];

        for (int i = 1; i < chars1.length+1; i++)
            mat[i][0] = i;


        for (int j = 1; j < chars2.length+1; j++)
            mat[0][j] = j;

        for (int j = 1; j < chars2.length+1; j++) {
            for (int i = 1; i < chars1.length+1; i++) {
                int cost = 0;
                if(chars1[i-1] != chars2[j-1])
                    cost = 1;
                mat[i][j] = Math.min(mat[i-1][j] + 1, mat[i][j-1] + 1);
                mat[i][j] = Math.min(mat[i][j], mat[i-1][j-1] + cost);

            }
        }
        distance = mat[chars1.length][chars2.length];


        return distance;
    }

    private static double getWordSimilarity(String s1, String s2) {
        int distance = getWordDistance(s1, s2);
        int longerWordLength = Math.max(s1.length(), s2.length());
        int sim = longerWordLength - distance;

        double similarity = (double) sim/longerWordLength;
        if (similarity < 0) similarity = 0;

        return similarity;
    }
    private static int getSentenceDistance(String s1, String s2) {
        int distance = 0;

        String[] l1 = s1.split(" ");
        String[] l2 = s2.split(" ");

        int[][] mat = new int[l1.length+1][l2.length+1];

        for (int i = 1; i < l1.length+1; i++)
            mat[i][0] = i;

        for (int j = 1; j < l2.length+1; j++)
            mat[0][j] = j;

        for (int j = 1; j < l2.length+1; j++) {
            for (int i = 1; i < l1.length+1; i++) {
                int cost = 0;
                if(!l1[i-1].equals(l2[j-1]) )
                    cost = 1;
                mat[i][j] = Math.min(mat[i-1][j] + 1, mat[i][j-1] + 1);
                mat[i][j] = Math.min(mat[i][j], mat[i-1][j-1] + cost);

            }
        }
        distance = mat[l1.length][l2.length];


        return distance;
    }

    private static double getSentenceSimilarity(String s1, String s2) {
        int distance = getSentenceDistance(s1, s2);
        int longerWordLength = Math.max(s1.split(" ").length, s2.split(" ").length);
        int sim = longerWordLength - distance;

        double similarity = (double) sim/longerWordLength;
        if (similarity < 0) similarity = 0;
        return similarity;
    }


    private static String findMostSimilarInput(String s, HashSet<String> set){
        String result = "";
        Iterator<String> i = set.iterator();
        double maxSim = 0.0;

        while (i.hasNext()) {
            String cur = i.next();
            double similarity = getWordSimilarity(cur, s);
            if (similarity > maxSim) {
                result = cur;
                maxSim = similarity;
            }

        }

        return result;
    }

    private static void printSets() {
        Iterator<String> itH = holidayType.iterator();
        Iterator<String> itR = region.iterator();
        Iterator<String> itA = accom.iterator();
        Iterator<String> itT = transportation.iterator();

        while (itH.hasNext()) {
            System.out.println(itH.next());
        }


        while (itR.hasNext()) {
            System.out.println(itR.next());
        }


        while (itA.hasNext()) {
            System.out.println(itA.next());
        }


        while (itT.hasNext()) {
            System.out.println(itT.next());
        }


    }

    public static void main(String[] args) {
	    constructCases();
        constructWeights();
        constructRegions();

        System.out.println("I can help you to plan your trip!");
        boolean finish = false;

        while (!finish) {
            Case userCase = obtainCase();
            ArrayList<Map.Entry<Double, Case>> topCases = retrieveCases(userCase);
            returnResults(topCases);
            System.out.println("Would you like to plan another trip?");
            System.out.println("(Yes/No)");
            Scanner s = new Scanner(System.in);
            String item = s.next();
            if (item.equals("no") ||item.equals("No") || item.equals("NO") || item.length() == 0) {
                break;
            }
        }

        System.out.println("Thank you!");

    }


}
