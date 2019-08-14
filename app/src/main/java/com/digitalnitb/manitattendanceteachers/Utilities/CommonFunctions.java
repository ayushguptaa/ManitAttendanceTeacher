package com.digitalnitb.manitattendanceteachers.Utilities;


import android.content.Context;

import com.digitalnitb.manitattendanceteachers.LayoutFragments.SeatAdapter;

import java.util.ArrayList;

public class CommonFunctions {

    private static String mBranch;
    private static String mYear;
    private static String mSubject;

    public static void setmBranch(String branch, String year, String subject) {
        mBranch = branch;
        mYear = year;
        mSubject = subject;
    }

    public static String getBranch() {
        return mBranch;
    }

    public static String getYear() {
        return mYear;
    }

    public static String getSubject() {
        return mSubject;
    }

    public static String BeautifyName(String str) {
        String[] words = str.split(" ");
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            ret.append(Character.toUpperCase(words[i].charAt(0)));
            ret.append(words[i].substring(1));
            if (i < words.length - 1) {
                ret.append(' ');
            }
        }
        return ret.toString();
    }

    public static String getYearFromSem(String sem) {
        switch (sem) {
            case "I":
            case "II":
                return "1";
            case "III":
            case "IV":
                return "2";
            case "V":
            case "VI":
                return "3";
            case "VII":
            case "VIII":
                return "4";
            case "M-Tech-I":
            case "M-Tech-II":
                return "M-Tech-1";
            default:
                return "";
        }
    }

    private static ArrayList<SeatAdapter> seatAdapters = new ArrayList<>();

    public static void addAdapter(SeatAdapter adapter) {
        seatAdapters.add(adapter);
    }

    public static void notifyAllAdapters() {
        for (SeatAdapter adapter : seatAdapters) {
            adapter.notifyDataSetChanged();
        }
    }


    //All functions for getting subjects
    private static String mSelecedSubject;

    private static final int BRANCH_ARCH = 0;
    private static final int BRANCH_CHEM = 1;
    private static final int BRANCH_CIVIL = 2;
    private static final int BRANCH_CSE1 = 3;
    private static final int BRANCH_CSE2 = 4;
    private static final int BRANCH_ECE = 5;
    private static final int BRANCH_ELEC = 6;
    private static final int BRANCH_MECH1 = 7;
    private static final int BRANCH_MECH2 = 8;
    private static final int BRANCH_MSME = 9;
    private static final int MTECH_ADVANCED_COMPUTING = 10;
    private static final int MTECH_COMPUTER_NETWORKS = 11;
    private static final int MTECH_INFORMATION_SECURITY = 10;

    private static final String[] SUB_LIST_1_I = new String[]{"Basic Civil", "Comm Skills", "Comp Prog", "Engg Graphic", "Maths 1", "Physics"};
    private static final String[] SUB_LIST_2_I = new String[]{"Basic E & Elect", "Basic Mech", "Engg Chem", "Env Engg", "Maths 1", "Solid Mech"};
    private static final String[] SUB_LIST_1_II = new String[]{"Basic Civil", "Comm Skills", "Comp Prog", "Engg Graphic", "Maths 2", "Physics"};
    private static final String[] SUB_LIST_2_II = new String[]{"Basic E & Elect", "Basic Mech", "Engg Chem", "Env Engg", "Maths 2", "Solid Mech"};


    public static String[] getSubjects(int branch, int semester) {
        if (branch == BRANCH_ARCH) {
            return new String[]{};
        }
        switch (semester) {
            case 1:
                if (branch == BRANCH_CHEM || branch == BRANCH_CSE1 || branch == BRANCH_CSE2 || branch == BRANCH_ECE) {
                    return SUB_LIST_2_I;
                } else {
                    return SUB_LIST_1_I;
                }
            case 2:
                if (branch == BRANCH_CHEM || branch == BRANCH_CSE1 || branch == BRANCH_CSE2 || branch == BRANCH_ECE) {
                    return SUB_LIST_1_II;
                } else {
                    return SUB_LIST_2_II;
                }
            default:
                return new String[]{};
        }
    }

    public static String getClass(int branch, int semester) {
        switch (branch) {
            case BRANCH_CHEM:
                return "TB-214:6:10";
            case BRANCH_CIVIL:
                return "TB-216:6:10";
            case BRANCH_CSE1:
                return "TB-111:6:10";
            case BRANCH_CSE2:
                return "TB-112:6:10";
            case BRANCH_ECE:
                return "TB-211:6:10";
            case BRANCH_ELEC:
                return "TB-116:6:10";
            case BRANCH_MECH1:
                return "TB-113:6:10";
            case BRANCH_MECH2:
                return "TB-114:6:10";
            case BRANCH_MSME:
                return "TB-214:6:10";
            default:
                return "Unknown:6:10";
        }
    }
}
