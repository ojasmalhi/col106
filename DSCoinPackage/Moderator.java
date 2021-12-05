package DSCoinPackage;

import HelperClasses.*;
import java.util.*;

public class Moderator {
  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
    Members moderator = new Members();
    moderator.UID = "Moderator";
    int n_blocktxns = DSObj.bChain.tr_count;
    int n_members = DSObj.memberlist.length;
    Transaction[] txn_arr = new Transaction[coinCount];
    
    //construct array of txns
    for (int i = 0; i < coinCount; i++) {
      Transaction t = new Transaction();
      t.coinID = Integer.toString(100000 + i);
      t.Source = moderator;
      t.Destination = DSObj.memberlist[i % n_members];
      t.coinsrc_block = null;
      txn_arr[i] = t;
    }

    //construct txnblocks
    for (int i = 0; i < coinCount / n_blocktxns; i++) {
      Transaction[] temp = Arrays.copyOfRange(txn_arr, i * n_blocktxns, (i + 1) * n_blocktxns);
      TransactionBlock tB = new TransactionBlock(temp);
      DSObj.bChain.InsertBlock_Honest(tB);
      for (int j = 0; j < n_blocktxns; j++){
        Pair<String, TransactionBlock> coin = new Pair<String, TransactionBlock>(Integer.toString(100000 + j + i * n_blocktxns), tB);
        DSObj.memberlist[(i * n_blocktxns + j) % n_members].mycoins.add(coin);
      }
    }

    //set latestCoinID of DSObj
    int last = 100000 + coinCount - 1;
    DSObj.latestCoinID = Integer.toString(last); 
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
    Members moderator = new Members();
    moderator.UID = "Moderator";
    int n_blocktxns = DSObj.bChain.tr_count;
    int n_members = DSObj.memberlist.length;
    Transaction[] txn_arr = new Transaction[coinCount];
    
    //construct array of txns
    for (int i = 0; i < coinCount; i++) {
      Transaction t = new Transaction();
      t.coinID = Integer.toString(100000 + i);
      t.Source = moderator;
      t.Destination = DSObj.memberlist[i % n_members];
      t.coinsrc_block = null;
      txn_arr[i] = t;
    }

    //construct txnblocks
    for (int i = 0; i < coinCount / n_blocktxns; i++) {
      Transaction[] temp = Arrays.copyOfRange(txn_arr, i * n_blocktxns, (i + 1) * n_blocktxns);
      TransactionBlock tB = new TransactionBlock(temp);
      DSObj.bChain.InsertBlock_Malicious(tB);
      for (int j = 0; j < n_blocktxns; j++){
        Pair<String, TransactionBlock> coin = new Pair<String, TransactionBlock>(Integer.toString(100000 + j + i * n_blocktxns), tB);
        DSObj.memberlist[(i * n_blocktxns + j) % n_members].mycoins.add(coin);
      }
    }

    //set latestCoinID of DSObj
    int last = 100000 + coinCount - 1;
    DSObj.latestCoinID = Integer.toString(last); 
  }
}
