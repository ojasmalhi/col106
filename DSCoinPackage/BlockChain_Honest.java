package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    CRF crf64 = new CRF(64);
    String str = "";

    //finding nonce
    int x = 1000000001;
    if (this.lastBlock == null) {
      str = this.start_string + "#" + newBlock.trsummary + "#";
    }
    else {
      str = this.lastBlock.dgst + "#" + newBlock.trsummary + "#";
    }
    while (!crf64.Fn(str + Integer.toString(x)).substring(0, 4).equals("0000")) {
      x++;
    }
    newBlock.nonce = Integer.toString(x);

    //set newBlock.dgst
    newBlock.dgst = crf64.Fn(str + newBlock.nonce);

    //set previous pointers
    if (this.lastBlock == null) {
      this.lastBlock = newBlock;
      this.lastBlock.previous = null;
    }
    else {
      newBlock.previous = this.lastBlock;
      this.lastBlock = newBlock;
    }
  }
}
