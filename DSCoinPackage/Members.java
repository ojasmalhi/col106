package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;

public class Members {
  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins = new ArrayList<Pair<String, TransactionBlock>>();
  public Transaction[] in_process_trans = new Transaction[100];

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    //initialize and remove .coinID from mycoins
    Collections.sort(mycoins, Comparator.comparing(p -> Integer.parseInt(p.get_first())));
    Pair<String, TransactionBlock> coin = mycoins.remove(0);

    //initialize tobj
    Transaction tobj = new Transaction();
    tobj.coinID = coin.get_first();
    tobj.Source = this;
    for (int i = 0; i < DSobj.memberlist.length; i++) {
      if (DSobj.memberlist[i].UID.equals(destUID)) {
        tobj.Destination = DSobj.memberlist[i];
        break;
      }
    }
    tobj.coinsrc_block = coin.get_second();

    //add to in_process_trans
    int idx = 0;
    while (in_process_trans[idx] != null) {
      idx++;
    }
    in_process_trans[idx] = tobj;

    //add to pendingTransactions
    DSobj.pendingTransactions.AddTransactions(tobj);
  }

  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
    //initialize and remove .coinID from mycoins
    Collections.sort(mycoins, Comparator.comparing(p -> Integer.parseInt(p.get_first())));
    Pair<String, TransactionBlock> coin = mycoins.remove(0);

    //initialize tobj
    Transaction tobj = new Transaction();
    tobj.coinID = coin.get_first();
    tobj.Source = this;
    for (int i = 0; i < DSobj.memberlist.length; i++) {
      if (DSobj.memberlist[i].UID.equals(destUID)) {
        tobj.Destination = DSobj.memberlist[i];
        break;
      }
    }
    tobj.coinsrc_block = coin.get_second();

    //add to in_process_trans
    int idx = 0;
    while (in_process_trans[idx] != null) {
      idx++;
    }
    in_process_trans[idx] = tobj;

    //add to pendingTransactions
    DSobj.pendingTransactions.AddTransactions(tobj);
  }


  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    //find block containing tobj and construct block path
    List<Pair<String, String>> blockchain_path = new ArrayList<Pair<String, String>>();
    TransactionBlock block = DSObj.bChain.lastBlock;
    
    while (block.containsTransaction(tobj) == -1) {
      if (block.previous == null) {
        throw new MissingTransactionException();
      }
      Pair<String, String> block_pair = new Pair<String, String>(block.dgst, block.previous.dgst + "#" + block.trsummary + "#" + block.nonce);
      blockchain_path.add(block_pair);
      block = block.previous;
    }

    blockchain_path.add(new Pair<String, String>(block.dgst, block.previous.dgst + "#" + block.trsummary + "#" + block.nonce));

    if (block.previous == null) {
      blockchain_path.add(new Pair<String, String>(DSObj.bChain.start_string, null));
    }
    else {
      blockchain_path.add(new Pair<String, String>(block.previous.dgst, null));
    }
    Collections.reverse(blockchain_path);

    int idx = block.containsTransaction(tobj);

    //sibling coupled path
    List<Pair<String, String>> tree_path = new ArrayList<Pair<String, String>>();
    tree_path = block.merkleProof(idx);

    //add coin to dest's mycoins
    Pair<String, TransactionBlock> coin = new Pair<String, TransactionBlock>(tobj.coinID, block);
    tobj.Destination.mycoins.add(coin);
    Collections.sort(tobj.Destination.mycoins, Comparator.comparing(p -> Integer.parseInt(p.get_first())));

    //remove tobj from inprocesstrxns
    if (tobj.Source.UID.equals("Moderator") == false) {
      for (int i = 0; i < 100; i++) {
        if (this.in_process_trans[i] != null && tobj.coinID.equals(this.in_process_trans[i].coinID) && tobj.Source.UID.equals(this.in_process_trans[i].Source.UID) && tobj.Destination.UID.equals(this.in_process_trans[i].Destination.UID) && tobj.coinsrc_block.trsummary.equals(this.in_process_trans[i].coinsrc_block.trsummary)) {
          this.in_process_trans[i] = null;
          break;
        }
      }
    }

    return new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(tree_path, blockchain_path);
  }

  public void MineCoin(DSCoin_Honest DSObj) {
    int count = 0;
    int n_blocktxns = DSObj.bChain.tr_count;
    Transaction[] txnarr = new Transaction[n_blocktxns];
    TransactionBlock block = DSObj.bChain.lastBlock;
    try{
      // add n-1 valid txns to txnarr
      while (count < n_blocktxns - 1) {
        Transaction txn = DSObj.pendingTransactions.RemoveTransaction();
        if (block.checkTransaction(txn)) {
          int flag = 0;
          // check for duplicates
          for (int i = 0; i < count; i++) {
            if (txn.coinID.equals(txnarr[i])) {
              flag = 1;
              break;
            }
          }
          if (flag == 0) {
            txnarr[count] = txn;
            count++;
          }
        }
      }

      //create miner reward txn
      Transaction reward = new Transaction();
      reward.coinID = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);
      DSObj.latestCoinID = reward.coinID; //set latestcoinID
      reward.Destination = this;
      reward.Source = null;
      txnarr[n_blocktxns - 1] = reward;
      TransactionBlock tB = new TransactionBlock(txnarr);
      DSObj.bChain.InsertBlock_Honest(tB); //insert block into blockchain
      this.mycoins.add(new Pair<String, TransactionBlock>(reward.coinID, tB)); //add coin to miner.mycoins
    }
    catch (Exception e) {

    }
  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
    int count = 0;
    int n_blocktxns = DSObj.bChain.tr_count;
    Transaction[] txnarr = new Transaction[n_blocktxns];
    TransactionBlock longest_chain_block = DSObj.bChain.FindLongestValidChain();
    // add n-1 valid txns to txnarr
    try{
      while (count < n_blocktxns - 1) {
        Transaction txn = DSObj.pendingTransactions.RemoveTransaction();
        if (longest_chain_block.checkTransaction(txn)) {
          int flag = 0;
          // check for duplicates
          for (int i = 0; i < count; i++) {
            if (txn.coinID.equals(txnarr[i])) {
              flag = 1;
              break;
            }
          }
          if (flag == 0) {
            txnarr[count] = txn;
            count++;
          }
        }
      }

      //create miner reward txn
      Transaction reward = new Transaction();
      reward.coinID = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);
      DSObj.latestCoinID = reward.coinID; //set latestcoinID
      reward.Destination = this;
      reward.Source = null;
      txnarr[n_blocktxns - 1] = reward;
      TransactionBlock tB = new TransactionBlock(txnarr);
      DSObj.bChain.InsertBlock_Malicious(tB); //insert block into blockchain
      this.mycoins.add(new Pair<String, TransactionBlock>(reward.coinID, tB)); //add coin to miner.mycoins
    }

    catch (Exception e) {
        
    }
  }  
}
