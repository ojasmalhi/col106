package DSCoinPackage;

import DSCoinPackage.*;
import HelperClasses.*;
import java.util.*;

public class Test {
    public static void main(String[] args){
        CRF obj = new CRF(64);
        Transaction[] t = new Transaction[1];
        Transaction tra =  new Transaction();
        tra.coinID = "100000";
        Members dest = new Members();
        dest.UID = "101";
        tra.Destination = dest;
        t[0]=tra;
        Transaction[] t3 = new Transaction[1];
        Transaction tra1 =  new Transaction();
        tra1.coinID = "100001";
        Members dest1 = new Members();
        dest1.UID = "102";
        tra1.Destination = dest;
        t3[0]=tra1;
        TransactionBlock t1 = new TransactionBlock(t);
        TransactionBlock t2 = new TransactionBlock(t3);
        BlockChain_Honest b = new BlockChain_Honest();
        //b.InsertBlock_Honest(t1);
        //b.InsertBlock_Honest(t2);
        // System.out.println("nonce1: " + t1.nonce);
        // System.out.println("dgst1: " + t1.dgst);
        // System.out.println("nonce2: " + t2.nonce);
        // System.out.println("dgst2: " + t2.dgst);
        //System.out.println(t1.checkTransaction(t1.trarray[0]));
        TransactionQueue q = new TransactionQueue();
        q.AddTransactions(tra);
        q.AddTransactions(tra1);
        try {
            System.out.println(q.lastTransaction.coinID);
            System.out.println(q.RemoveTransaction().coinID);
        }
        catch(EmptyQueueException e){}
    }
}