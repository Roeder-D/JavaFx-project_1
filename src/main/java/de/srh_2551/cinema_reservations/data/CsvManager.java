package de.srh_2551.cinema_reservations.data;


import de.srh_2551.cinema_reservations.model.Hall;
import de.srh_2551.cinema_reservations.model.Row;
import de.srh_2551.cinema_reservations.model.Seat;

import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CsvManager {
    //Set up Logger
    private static final Logger LOGGER = Logger.getLogger(CsvManager.class.getName());

    //configuration
    private static final String CSV_HEADER = "RowName,RowId,TotalSeats,GapInFront,SeatNumber,SeatType,SeatStatus";
    private static final String FOLDER_PATH = "data/";

    //Saving hall to CSV
    public static void saveHall(Hall hall){
        File folder = new File(FOLDER_PATH);
        if(!folder.exists()){
            throw new RuntimeException("Directory doesn't exist");
        }
        //creating file name and removing spaces
        String fileName = FOLDER_PATH + hall.getName().replaceAll("\\s+", "_") + ".csv";


        System.out.println("Saving Hall: " + hall.getName() + " to " + fileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))){

            writer.println("HallName: " + hall.getName());
            writer.println(CSV_HEADER);

            for (Row row : hall.getRows()) {
                int totalSeatsInRow = row.getSeats().size();

                //Writing a single seat in a line
                for (Seat seat : row.getSeats()) {
                    String line = String.format("%s,%d,%d,%b,%d,%s,%s",
                            row.getRowIdentifier(),
                            row.getRowId(),
                            totalSeatsInRow,
                            row.getGapInFront(),
                            seat.getSeatNumber(),
                            seat.getSeatType().name(),
                            seat.getSeatStatus().name()
                    );
                    writer.println(line);
                }
            }
            System.out.println("Save successful!");
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "Critical Error saving to CSV: " + fileName, e);
        }
    }

    //Loading from CSV
    public static Hall loadHall(String hallName){
        //creating file name and removing spaces
        String fileName = FOLDER_PATH + hallName.replaceAll("\\s+", "_") + ".csv";
        File file = new File(fileName);

        if(!file.exists()) {
            System.err.println("File does not exist! Creating new empty hall.");
            return new Hall(hallName);
        }

        Hall hall;
        //Open file reader using try-with-resources
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            //extract hallName from file
            String metadataLine = reader.readLine();
            String extractedHallName = metadataLine.split(":")[1].trim();

            hall =new Hall(extractedHallName);

            //Skip header
            reader.readLine();

            //Read the rest
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                // Safety check to make sure the line contains the right amount of data
                if (data.length != 7) continue;

                // Extract the data from the array
                String rowName = data[0];
                int rowId = Integer.parseInt(data[1]);
                int totalSeats = Integer.parseInt(data[2]);
                boolean gapInFront = Boolean.parseBoolean(data[3]);
                int seatNum = Integer.parseInt(data[4]);


                //Convert Strings back into Enums
                Seat.SeatType seatType = Seat.SeatType.valueOf(data[5]);
                Seat.SeatStatus seatStatus = Seat.SeatStatus.valueOf(data[6]);

                //Reconstruct Objects
                //Check if row already exists
                Row currentRow = hall.getRow(rowId);

                if (currentRow == null) {
                    currentRow = new Row(rowName, rowId, totalSeats, seatType, gapInFront);
                    hall.addRow(currentRow);
                }

                //Apply the specific seat's status
                Seat seat = currentRow.getSeatByNumber(seatNum);
                if (seat != null) {
                    seat.setSeatType(seatType);
                    seat.setSeatStatus(seatStatus);
                }
            }
            System.out.println("Load successful!");
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "Critical Error loading from CSV: " + fileName, e);
            hall = new Hall(hallName);
        }

        return hall;

    }

    public static List<String> getAllHallNames() {
        List<String> hallNames = new ArrayList<>();

        File folder = new File(FOLDER_PATH);

        if (!folder.exists()) {
            throw new RuntimeException("Folder does not exist!");
        }

        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                //Check if it's a .csv
                if (file.isFile() && file.getName().endsWith(".csv")) {

                    //Get the file name
                    String hallName = file.getName();

                    //Recreate original name
                    hallName = hallName.replace(".csv", "").replace("_", " ");

                    //Add to list
                    hallNames.add(hallName);
                }
            }
        }

        return hallNames;
    }

}
