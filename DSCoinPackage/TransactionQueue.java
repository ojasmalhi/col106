package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions(Transaction transaction) {
    //empty
    if (this.firstTransaction == null) {
      this.firstTransaction = transaction;
      this.lastTransaction = transaction;
    }
    //not empty
    else {
      transaction.previous = this.lastTransaction;
      this.lastTransaction.next = transaction;
      this.lastTransaction = transaction;
    }
    this.numTransactions++;
  }
  
  public Transaction RemoveTransaction() throws EmptyQueueException {
    //empty
    if (this.firstTransaction == null) {
      throw new EmptyQueueException();
    }
    //single element
    if (this.firstTransaction == this.lastTransaction) {
      Transaction t = this.firstTransaction;
      this.firstTransaction = this.lastTransaction = null;
      this.numTransactions--;
      return t;
    }
    // >1 elements
    else {
      Transaction t = this.firstTransaction;
      this.firstTransaction = this.firstTransaction.next;
      this.firstTransaction.previous = null;
      this.numTransactions--;
      return t;
    }
  }

  public int size() {
    return this.numTransactions;
  }
}
