package ussdtranscom.bsk.transferokredit;

import java.util.ArrayList;

public class KtheVlerenTeIma {

    public ArrayList<Integer> merrListenEVlerave(int testo) {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int i = 0; i < ndaj(testo).size(); i++) {
            int numri = new Integer(ndaj(testo).get(i).split("\\|")[1]).intValue();
            if (numri != 0)
                if (numri > 1 & (i <= ndaj(testo).size() - 2)) {
                    int vlera = new Integer(ndaj(testo).get(i).split("\\|")[0]).intValue();
                    for (int j = 0; j < numri; j++)
                        temp.add(vlera);
                } else if (i <= ndaj(testo).size() - 2) {
                    int vlera = new Integer(ndaj(testo).get(i).split("\\|")[0]).intValue();
                    temp.add(vlera);
                }
        }
        return temp;
    }

    private ArrayList<String> ndaj(int nr) {
        ArrayList<String> temp = new ArrayList<String>();
        int numri = nr, count = 0;

        if (numri / 50 != 0)
            count += (numri / 50);
        temp.add(50 + "|" + (numri / 50));
        numri = numri % 50;

        if (numri / 40 != 0)
            count += (numri / 40);
        temp.add(40 + "|" + (numri / 40));
        numri = numri % 40;

        if (numri / 20 != 0)
            count += (numri / 20);
        temp.add(20 + "|" + (numri / 20));
        numri = numri % 20;

        if (numri / 10 != 0)
            count += (numri / 10);
        temp.add(10 + "|" + (numri / 10));
        numri = numri % 10;

        if (numri / 5 != 0)
            count += (numri / 5);
        temp.add(5 + "|" + (numri / 5));
        numri = numri % 5;

        if (numri / 3 != 0)
            count += (numri / 3);
        temp.add(3 + "|" + (numri / 3));
        numri = numri % 3;

        if (numri / 2 != 0)
            count += (numri / 2);
        temp.add(2 + "|" + (numri / 2));
        numri = numri % 2;

        if (numri / 1 != 0)
            count += (numri / 1);
        temp.add(1 + "|" + (numri / 1));
        numri = numri % 1;

        temp.add("total|" + count);
        return temp;
    }
}