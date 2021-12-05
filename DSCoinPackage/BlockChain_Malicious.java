package DSCoinPackage;

import HelperClasses.*;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList = new TransactionBlock[100];

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    CRF crf64 = new CRF(64);
    String corr_dgst = "";

    //check dgst
    if (!tB.dgst.substring(0, 4).equals("0000")) {
      return false;
    }
    if (tB.previous == null) {
      corr_dgst = crf64.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce);
    }
    else {
      corr_dgst = crf64.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce);
    }

    if (!tB.dgst.equals(corr_dgst)) {
        return false;
      }

    //check trsummary
    MerkleTree tree = new MerkleTree();
    if (!tB.trsummary.equals(tree.Build(tB.trarray))) {
      return false;
    }

    //check validity of txns
    for (int i = 0; i < tB.trarray.length; i++) {
      if (tB.checkTransaction(tB.trarray[i]) ==  false) {
        return false;
      }
    }

    return true;
  }

  public TransactionBlock FindLongestValidChain () {
    int max = 0;
    TransactionBlock res = null;
    int l = this.lastBlocksList.length;
    for (int i = 0; i < l; i++) {
      TransactionBlock block = this.lastBlocksList[i];
      if (block == null) {
        break;
      }
      else {
        int j = 0;
        TransactionBlock temp = block;
        while (temp.previous != null) {
          if (checkTransactionBlock(temp) == true) {
            j++;
            temp = temp.previous;
          }
          else {
            j = 0;
            temp = temp.previous;
            block = temp;
          }
        }
        j++;
        if (j > max) {
          max = j;
          res = block;
        }
      }
    }
    return res;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
    CRF crf64 = new CRF(64);
    String str = "";
    TransactionBlock lastBlock = this.FindLongestValidChain();

    //nonce calculation
    int x = 1000000001;
    if (lastBlock == null) {
      str = this.start_string + "#" + newBlock.trsummary + "#";
    }
    else {
      str = lastBlock.dgst + "#" + newBlock.trsummary + "#";
    }
    while (!crf64.Fn(str + Integer.toString(x)).substring(0, 4).equals("0000")) {
      x++;
    }
    newBlock.nonce = Integer.toString(x);

    //set newblock dgst
    newBlock.dgst = crf64.Fn(str + newBlock.nonce);

    //add to chain/tree
    for (int i = 0; i < this.lastBlocksList.length; i++) {
      if (this.lastBlocksList[i] == lastBlock || this.lastBlocksList[i] == null) {
        this.lastBlocksList[i] = newBlock;
        break;
      }
    }
    newBlock.previous = lastBlock;
  }
}
