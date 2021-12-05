package HelperClasses;

import DSCoinPackage.Transaction;
import java.util.*;

public class MerkleTree {

  // Check the TreeNode.java file for more details
  public TreeNode rootnode;
  public int numdocs;

  void nodeinit(TreeNode node, TreeNode l, TreeNode r, TreeNode p, String val) {
    node.left = l;
    node.right = r;
    node.parent = p;
    node.val = val;
  }

  public String get_str(Transaction tr) {
    CRF obj = new CRF(64);
    String val = tr.coinID;
    if (tr.Source == null)
      val = val + "#" + "Genesis"; 
    else
      val = val + "#" + tr.Source.UID;

    val = val + "#" + tr.Destination.UID;

    if (tr.coinsrc_block == null)
      val = val + "#" + "Genesis";
    else
      val = val + "#" + tr.coinsrc_block.dgst;

    return obj.Fn(val);
  }

  public String Build(Transaction[] tr) {
    CRF obj = new CRF(64);
    int num_trans = tr.length;
    numdocs = num_trans;
    List<TreeNode> q = new ArrayList<TreeNode>();
    for (int i = 0; i < num_trans; i++) {
      TreeNode nd = new TreeNode();
      String val = get_str(tr[i]);
      nodeinit(nd, null, null, null, val);
      q.add(nd);
    }
    TreeNode l, r;
    while (q.size() > 1) {
      l = q.get(0);
      q.remove(0);
      r = q.get(0);
      q.remove(0);
      TreeNode nd = new TreeNode();
      String l_val = l.val;
      String r_val = r.val;
      String data = obj.Fn(l_val + "#" + r_val);
      nodeinit(nd, l, r, null, data);
      l.parent = nd;
      r.parent = nd;
      q.add(nd);
    }
    rootnode = q.get(0);

    return rootnode.val;
  }

  public List<Pair<String, String>> siblingCoupledPath(int idx) {
    int levels = (int)((Math.log(this.numdocs) + 1e-11) / Math.log(2)) + 1;
    
    //finding binary representation
		int bin_rep[] = new int[levels - 1];
		for (int i = levels - 2; i >= 0; i--) {
			bin_rep[i] = idx % 2;
			idx /= 2;
		}

    //constructing sibling coupled path
		List<Pair<String, String>> query = new ArrayList<Pair<String, String>>(levels);
		TreeNode node = rootnode;
		query.add(0, new Pair<String, String>(node.val, null));
		for (int i = 0; i < levels - 1; i++) {
			query.add(i + 1, new Pair<String, String>(node.left.val, node.right.val));
			if (bin_rep[i] == 0) {
				node = node.left;
			}
			else {
				node = node.right;
			}
		}
		Collections.reverse(query);
		return query;
  }
}
