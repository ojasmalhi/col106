package DSCoinPackage;

import HelperClasses.*;
import java.util.*;

public class TransactionBlock {
  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
    //set trarray
    this.trarray = new Transaction[t.length];
    for (int i = 0; i < t.length; i++) {
      this.trarray[i] = t[i];
    }

    //set Tree and trsummary
    Tree = new MerkleTree();
    this.trsummary = this.Tree.Build(this.trarray);
  }

  public int containsTransaction(Transaction t) {
    for (int i = 0; i < this.trarray.length; i++) {
      if (this.trarray[i].coinID.equals(t.coinID) && this.trarray[i].Source.UID.equals(t.Source.UID) && this.trarray[i].Destination.UID.equals(t.Destination.UID) && this.trarray[i].coinsrc_block.trsummary.equals(t.coinsrc_block.trsummary)) {
        return i;
      }
    }
    return -1;
  }

  public boolean checkTransaction (Transaction t) {
    // //check coinsrc_block contains coin sent to buyer txn
    // if (t.coinsrc_block == null) {
    //   return true;
    // }
    // boolean flag = false;
    // TransactionBlock source_block = t.coinsrc_block;
    // for (int i = 0; i < source_block.trarray.length; i++) {
    //   if (source_block.trarray[i].coinID.equals(t.coinID) && source_block.trarray[i].Destination.UID.equals(t.Source.UID)) {
    //     flag = true;
    //     break;
    //   }
    // }
    // if (flag == false) {
    //   return false;
    // }

    // //check .coinID not spent in intermediate blocks
    // TransactionBlock block = this;
    // while (block != t.coinsrc_block) {
    //   for (int i = 0; i < block.trarray.length; i++) {
    //     if (block.trarray[i].coinID.equals(t.coinID)) {
    //       return false;
    //     }
    //   }
    //   block = block.previous;
    // }
    // return true;

    int flag = 0;
    int idx = 0;
    TransactionBlock block = this;
    TransactionBlock temp = this;
    while(block != null) {
      if(block == t.coinsrc_block) {
        int len = this.trarray.length;
        for(int i = 0; i < len; i++) {
          if(block.trarray[i].Destination.UID.equals(t.Source.UID) && block.trarray[i].coinID.equals(t.coinID)) {
            flag++;
            idx = i;
            break;
          }
        }
        if(flag == 0) {
          return false;
        }
        break;
      }
      block = block.previous;
    }
    while(temp != block) {
      for (int i = 0; i < temp.trarray.length; i++) {
        if (temp.trarray[i] == t){
          break;
        }
        if (t.coinID.equals(temp.trarray[i].coinID)) {
          return false;
        }
      }
      temp = temp.previous;
    }
    if (block != null) {
      int i = idx + 1; 
      while (i < block.trarray.length) {
        if(temp.trarray[i] == t){
          break;
        }
        if(t.coinID.equals(temp.trarray[i].coinID)){
          return false;
        }
        i++;
      }
    }
    return true;
  }  

  public List<Pair<String, String>> merkleProof(int idx) {
    return this.Tree.siblingCoupledPath(idx);
  }
}
