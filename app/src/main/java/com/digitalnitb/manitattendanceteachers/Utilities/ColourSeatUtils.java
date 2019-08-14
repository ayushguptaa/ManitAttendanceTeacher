package com.digitalnitb.manitattendanceteachers.Utilities;

import com.digitalnitb.manitattendanceteachers.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ColourSeatUtils {

    private static int mColumns = 5;
    private static int mRows = 10;

    private static final int SEAT_SINGLE = 1;
    private static final int SEAT_MORE = 2;

    public static int NO_OF_ISSUES = 0;

    private static ArrayList<ArrayList<Integer>> colourArray;
    private static ArrayList<ArrayList<ArrayList<String>>> stringArray;

    public static void clearArrays(){
        if(colourArray!=null){
            colourArray.clear();
            stringArray.clear();
        }
    }

    public static void setUpArrayList(int columns, int rows){
        clearArrays();
        mColumns = columns; mRows = rows;
        colourArray = new ArrayList<>();
        stringArray = new ArrayList<>();
        for(int i = 0; i<columns ; i++){
            colourArray.add(new ArrayList<Integer>());
            ArrayList<ArrayList<String>> temp1 = new ArrayList<>();
            for(int j = 0; j < rows*4 ; j++){
                colourArray.get(i).add(0);
                ArrayList<String> temp0 = new ArrayList<>();
                temp0.add("");
                temp1.add(temp0);
        }
            stringArray.add(temp1);

    }}

    public static void setBooked(String input){

        String [] data = input.split("-");

        int column = Integer.parseInt(data[1])-1;
        int row = Integer.parseInt(data[2]);

        if(colourArray.get(column).get(row)==SEAT_SINGLE){

            colourArray.get(column).set(row, SEAT_MORE);
            stringArray.get(column).get(row).add(data[0]);
            NO_OF_ISSUES++;

        }else if(colourArray.get(column).get(row)==SEAT_MORE){
            stringArray.get(column).get(row).add(data[0]);
        }
        else {

            colourArray.get(column).set(row, SEAT_SINGLE);
            ArrayList<String> tempArray = new ArrayList<>();
            tempArray.add(data[0]);
            stringArray.get(column).set(row, tempArray);
        }
    }

    public static int getColour(int column, int position){
        return colourLogic(colourArray.get(mColumns-column).get(mRows*4-position));
    }

    private static int colourLogic(Integer value) {
        if (value == 1) {
            return R.color.seatSelected;
        }
        if (value == 2) {
            return R.color.seatNotEmpty;
        }
        if (value > 2){
            return R.color.attendence_stop;
        }

        return R.color.seatEmpty;
    }

    public static void clickedOnSeat(int column, int position){
        column = mColumns-column;
        position = mRows*4 - position;
        int number = colourArray.get(column).get(position);
        if(number>2){
            colourArray.get(column).set(position, number-3);
        }else if(number!=0){
            colourArray.get(column).set(position, number+3);
        }
    }

    public static ArrayList<String> getScholarNumbers(int column, int position){
        return stringArray.get(mColumns-column).get(mRows*4-position);
    }

    public static void setScholarNumber(int column, int position, int index){
        ArrayList<String> temp = stringArray.get(mColumns-column).get(mRows*4-position);
        int i = 0;
        for(String s : temp){
            if(s.length()==4){
                s = s.substring(0,3);
                temp.set(i, s);
                NO_OF_ISSUES++;
            }
            i++;
        }
        temp.set(index-1, temp.get(index-1)+"s");
        NO_OF_ISSUES--;
    }

    public static HashMap<String, String> getAttendanceData(){
        HashMap<String, String> dataHash = new HashMap<>();
        for(int i = 0; i < mColumns ; i++){
            for(int j = 0 ; j < mRows*4 ; j++){
                int value = colourArray.get(i).get(j);
                if(value==1){
                    String sch_num = stringArray.get(i).get(j).get(0);
                    dataHash.put(sch_num, "P");
                }else if(value==2){
                    ArrayList<String> sch_nums = stringArray.get(i).get(j);
                    for(String sch_num:sch_nums){
                        if(sch_num.length()==4){
                            dataHash.put(sch_num.substring(0, 3), "P");
                        }else {
                            dataHash.put(sch_num, "2");
                        }
                    }
                }else if(value==4){
                    String sch_num = stringArray.get(i).get(j).get(0);
                    dataHash.put(sch_num, "2");
                }
            }
        }
        return dataHash;
    }

    public static int getColumns(){
        return mColumns;
    }
    public static int getRows(){
        return mRows;
    }

}
