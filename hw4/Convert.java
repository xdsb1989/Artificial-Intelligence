import java.util.Scanner;
class tree{
	String variable;
	String parenthesis;
	tree left_tree;
	tree right_tree;
}

public class Convert {
	static String output;
	public static void main (String args[]){
		Scanner in = new Scanner(System.in);
		while (true){
			System.out.println("input the logical sentencs:");
			String sentence = in.nextLine();
			convert_to_cnf(sentence);
			output = "";
		}
	}

	private static void convert_to_cnf(String sentence) {
		char log_sent [] = new char [sentence.length()];
		log_sent = sentence.toCharArray();
		log_sent = eliminate_space(log_sent);
		log_sent = check_bi_imply(log_sent);
		log_sent = eliminate_outside_neg(log_sent);
		System.out.println(log_sent);
		
		tree root = new tree();
		Build_tree(root, log_sent);
		
		if (cnf_tree(root)){
			System.out.println("original is a cnf");
			travel(root);
			add_parenthesis();
			System.out.println(output);
		}
		else {
			System.out.println("original is not a cnf");
			while (!cnf_tree(root)){
				fix_tree(root);
			}
			System.out.println("Now,it is a cnf");
			travel(root);
			//
			add_parenthesis();
			System.out.println(output);
		}
	}

	private static void add_parenthesis() {
		char temp [] = new char [output.length()];
		temp = output.toCharArray();
		int count=0;
		for (int i=0;i<temp.length;i++)
			if (temp[i] == '&')
				count++;
		if (count > 0){
			StringBuffer sb = new StringBuffer();
			sb.append('(');
			for (int i=0;i<temp.length;i++){
				if (temp[i] != '&')
					sb.append(temp[i]);
				else{
					sb.append(')');
					sb.append('&');
					sb.append('(');
				}
			}
			sb.append(')');
			output = sb.toString();
		}
	}

	private static boolean cnf_tree(tree root) {
		if (root!=null && root.variable.equals("|")){
			if ( root.left_tree.variable.equals("&") || root.right_tree.variable.equals("&"))
						return false;
			else
				return (cnf_tree(root.left_tree) && cnf_tree(root.right_tree));
		}
		else if (root!=null && root.variable.equals("&")){
			return (cnf_tree(root.left_tree) && cnf_tree(root.right_tree));
		}
		else{
			return true;
		}
	}

	private static void fix_tree(tree root) {
		
		if (root!=null && root.variable.equals("|")){
			if (root.left_tree.variable.equals("&")){
				tree node_1 = new tree();
				tree node_left = new tree();
				tree node_right = new tree();
				copy_node(root.right_tree, node_1);
				copy_node(root.left_tree.left_tree, node_left);
				copy_node(root.left_tree.right_tree, node_right);
				
				root.variable = "&";
				root.left_tree = new tree();
				root.right_tree = new tree();
				root.left_tree.variable = "|";
				root.right_tree.variable = "|";
				
				root.left_tree.left_tree = new tree();
				root.left_tree.right_tree = new tree();
				root.right_tree.left_tree = new tree();
				root.right_tree.right_tree = new tree();
				
				copy_node(node_left, root.left_tree.left_tree);
				copy_node(node_right, root.right_tree.left_tree);
				copy_node(node_1, root.left_tree.right_tree);
				copy_node(node_1, root.right_tree.right_tree);
			}
			else if (root.right_tree.variable.equals("&")){
				tree node_1 = new tree();
				tree node_left = new tree();
				tree node_right = new tree();
				copy_node(root.left_tree, node_1);
				copy_node(root.right_tree.left_tree, node_left);
				copy_node(root.right_tree.right_tree, node_right);
				
				root.variable = "&";
				root.left_tree = new tree();
				root.right_tree = new tree();
				root.left_tree.variable = "|";
				root.right_tree.variable = "|";
				
				root.left_tree.left_tree = new tree();
				root.left_tree.right_tree = new tree();
				root.right_tree.left_tree = new tree();
				root.right_tree.right_tree = new tree();
				
				copy_node(node_left, root.left_tree.right_tree);
				copy_node(node_right, root.right_tree.right_tree);
				copy_node(node_1, root.left_tree.left_tree);
				copy_node(node_1, root.right_tree.left_tree);
			}
			else{
				fix_tree(root.left_tree);
				fix_tree(root.right_tree);
			}
		}
		else if (root!=null && root.variable.equals("&")){
			fix_tree(root.left_tree);
			fix_tree(root.right_tree);
		}
	}

	private static void copy_node(tree src, tree node) {
		if (src!=null){
			node.variable = src.variable;
			if (src.left_tree != null){
				node.left_tree = new tree();
				copy_node(src.left_tree, node.left_tree);
			}
			if (src.right_tree != null){
				node.right_tree = new tree();
				copy_node(src.right_tree, node.right_tree);
			}
		}
	}

	private static void travel(tree root) {
		if (root!=null){
			travel(root.left_tree);
			if (output != null)
				output = output + root.variable;
			else
				output = root.variable;
			travel(root.right_tree);
			
		}
	}

	private static void Build_tree(tree root, char[] log_sent) {
		int flag = 0;
		int number = 0;
		for (int i=0;i<log_sent.length;i++){
			if (log_sent[i] == '|' || log_sent[i] == '&'){
				flag = 1;
				number++;
			}
		}
		if (flag == 0){
			StringBuffer sb = new StringBuffer();
			for (int i=0;i<log_sent.length;i++)
				sb.append(log_sent[i]);
			root.variable = sb.toString();
		}
		else{
			char left[] = null;
			char right[] = null;
			int pre_index = 0;
			int operate_index = 0;
			
			if (number == 1){
				for (int i=0;i<log_sent.length;i++){
					if (log_sent[i] == '&' || log_sent[i] == '|'){
						operate_index = i;
						break;
					}
				}
				if (log_sent[0] == '('){
					left = new char [operate_index - 1];
					right = new char [log_sent.length - operate_index - 2];
					for (int i=1,j=0;i<operate_index;i++,j++)
						left[j] = log_sent[i];
					for (int i=operate_index+1, j=0;i<log_sent.length-1;i++,j++)
						right[j] = log_sent[i];
				}
				else{
					left = new char [operate_index];
					right = new char [log_sent.length - operate_index - 1];
					for (int i=0;i<operate_index;i++)
						left[i] = log_sent[i];
					//System.out.println(log_sent.length+"---"+operate_index+"----"+right.length);
					for (int i=operate_index+1, j=0;i<log_sent.length;i++, j++)
						right[j] = log_sent[i];
				}
			}
			else{
			
				if (log_sent[0] == '('){
					int count = 1;
					for (int i=1;i<log_sent.length;i++){
						if (log_sent[i]==')')
							count--;
						else if (log_sent[i] == '(')
							count++;
						if (count == 0){
							pre_index = i-1;
							break;
						}
					}
					operate_index = pre_index + 2;
					left = new char [pre_index];
					for (int i = 1, j=0;i<=pre_index;i++,j++)
						left[j] = log_sent[i];
					
					
				}
				else if (log_sent[0] != '('){
					int count = 0;
					for (int i=0;i<log_sent.length;i++){
						if (log_sent[i]=='|' || log_sent[i] == '&')
							break;
						count++;
					}
					left = new char [count];
					for (int i=0;i<count;i++)
						left[i] = log_sent[i];
					operate_index = count;
				}
				
				if (log_sent[operate_index+1] != '('){
					int count=0;
					for (int i=operate_index+1;i<log_sent.length;i++)
						count++;
					right = new char[count];
					for (int i=operate_index+1, j=0;i<log_sent.length;i++, j++)
						right[j] = log_sent[i];
					
				}
				else if (log_sent[operate_index+1] == '('){
					int count = log_sent.length - operate_index - 3;
					right = new char [count];
					for (int i=operate_index+2, j=0;i<log_sent.length-1;i++, j++)
						right[j] = log_sent[i];
				}
			}
			root.variable = log_sent[operate_index]+"";
			root.left_tree = new tree();
			root.right_tree = new tree();
			Build_tree(root.left_tree, left);
			Build_tree(root.right_tree, right);
		}
		
		
	}

	private static char[] eliminate_space(char[] log_sent) {
		int count=0;
		for (int i = 0;i<log_sent.length;i++){
			if (log_sent[i] == ' ')
				count++;
		}
		char new_log_sent [] = new char [log_sent.length - count];
		for (int i = 0,j=0;i<new_log_sent.length && j<log_sent.length;j++){
			if (log_sent[j]!=' '){
				new_log_sent[i] = log_sent[j];
				i++;
			}
		}
		return new_log_sent;
	}
	
	private static char[] eliminate_outside_neg(char[] log_sent) {
		for (int i=0;i<log_sent.length;i++){
			if (log_sent[i] == '!'){
				if (log_sent[i+1] == '('){
					int pre_index, post_index = 0;
					
					pre_index = i+1;
					int count = 0;
					for (int j=i+1;j<log_sent.length;j++){
						if (log_sent[j]=='(')
							count++;
						else if (log_sent[j]==')')
							count--;
						
						if (count == 0){
							post_index = j;
							break;
						}
					}
					String str1;
					StringBuffer sb1 = new StringBuffer();
					for (int j=0;j<i;j++)
						sb1.append(log_sent[j]);
					str1 = sb1.toString();
					
					String str2;
					StringBuffer sb2 = new StringBuffer();
					for (int j=post_index+1;j<log_sent.length;j++)
						sb2.append(log_sent[j]);
					str2 = sb2.toString();
					//System.out.println(str1+"-------"+str2);
					String str;
					StringBuffer sb = new StringBuffer();
					for (int j=pre_index+1;j<post_index;j++){
						if (log_sent[j] == '|')
							sb.append('&');
						else if (log_sent[j] == '&')
							sb.append('|');
						else if (log_sent[j]=='!'){
							j=j+1;
							sb.append(log_sent[j]);
						}
						else{
							sb.append('!');
							sb.append(log_sent[j]);
						}
					}
					str = sb.toString();
					String new_string = str1 +"("+ str +")"+ str2;
					log_sent = new char [new_string.length()];
					log_sent = new_string.toCharArray();
					return eliminate_outside_neg(log_sent);
				}
			}
		}
		return log_sent;
	}
	
	private static char[] double_neg(char[] log_sent) {
		for (int i=0;i<log_sent.length;i++){
			if (log_sent[i] == '!' && log_sent[i+1] == '!'){
				StringBuffer sb = new StringBuffer();
				String str = null;
				for (int j=0;j<log_sent.length;j++){
					if (j != i && j!=i+1){
						sb.append(log_sent[j]);
					}
				}
				str = sb.toString();
				log_sent = str.toCharArray();
				return double_neg(log_sent);
			}
		}
		return log_sent;
	}

	private static char[] check_bi_imply(char[] log_sent) {
		log_sent = Bicond(log_sent);
		log_sent = Imply(log_sent);
		log_sent = double_neg(log_sent);
		
		int count=0;
		for (int i=0;i<log_sent.length;i++){
			if (log_sent[i]=='=')
				count++;
		}
		if (count == 0)
			return log_sent;
		else
			return check_bi_imply(log_sent);
	}
	
	private static char[] Imply(char[] log_sent) {
		int index = 0;
		for (int i=0;i<log_sent.length;i++){
			if (log_sent[i] == '=' && log_sent[i+1] == '>'){
				index = i;
				break;
			}
		}
		if (index>0){
			int pre_index = 0;
			int post_index =0;
			String str1=null,str2 = null;
			if (log_sent[index-1] != ')'){
				if (index - 2 >= 0 && log_sent[index-2] == '!'){
					StringBuffer sb1 = new StringBuffer();
					sb1.append(log_sent[index-2]);
					sb1.append(log_sent[index-1]);
					str1 = sb1.toString();
					pre_index = index-2;
				}
				else {
					StringBuffer sb1 = new StringBuffer();
					sb1.append(log_sent[index-1]);
					str1 = sb1.toString();
					pre_index = index-1;
				}
			}
			else if (log_sent[index-1] == ')'){
				int count=0;
				for (int i = index-1;i>=0;i--){
					if (log_sent[i] == ')')
						count++;
					else if (log_sent[i] == '(')
						count--;
					if (count == 0){
						pre_index = i;
						if (pre_index-1 >= 0 && log_sent[pre_index-1] == '!'){
							pre_index = pre_index-1;
						}
						break;
					}
				}
				StringBuffer sb1 = new StringBuffer();
				for (int i = pre_index;i<=index-1;i++){
					sb1.append(log_sent[i]);
				}
				str1 = sb1.toString();
			}
			
			if (log_sent[index+2] != '('){
				if (log_sent[index+2] == '!'){
					StringBuffer sb2 = new StringBuffer();
					sb2.append(log_sent[index+2]);
					sb2.append(log_sent[index+3]);
					str2 = sb2.toString();
					post_index = index + 3;
				}
				else if (log_sent[index+2] != '!'){
					StringBuffer sb2 = new StringBuffer();
					sb2.append(log_sent[index+2]);
					str2 = sb2.toString();
					post_index = index + 2;
				}
				
			}
			else if (log_sent[index+2] == '('){
				int count=0;
				for (int i=index+2;i<log_sent.length;i++){
					if (log_sent[i]=='(')
						count++;
					else if (log_sent[i]==')')
						count--;
					
					if (count==0){
						post_index = i;
						break;
					}
				}
				StringBuffer sb2 = new StringBuffer();
				for (int i = index+2;i<=post_index;i++){
					sb2.append(log_sent[i]);
				}
				str2 = sb2.toString();
			}
			StringBuffer sb1 = new StringBuffer();
			for (int i = 0;i<pre_index;i++)
				sb1.append(log_sent[i]);
			
			StringBuffer sb2 = new StringBuffer();
			for (int i = post_index+1;i<log_sent.length;i++)
				sb2.append(log_sent[i]);
			
			String str_part1 = sb1.toString();
			String str_part2 = sb2.toString();
			
			//System.out.println(str1+"------\n"+str2+"---------\n");
			//System.out.println(str_part1+"------\n"+str_part2+"---------\n");
			String new_str;
			String neg = "!";
			String or = "|";
			new_str = str_part1+neg+str1+or+str2+str_part2;
			log_sent = new char [new_str.length()];
			log_sent = new_str.toCharArray();
			return log_sent;
		}
		else{
			return log_sent;
		}
	}

	private static char[] Bicond(char[] log_sent) {
		int index=0;
		for (int i=0;i<log_sent.length;i++){
			if (log_sent[i] == '=' && log_sent[i-1] == '<' && log_sent[i+1] == '>'){
				index = i;
				break;
			}
		}
		if (index>0){
			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			for (int i=0;i<index-1;i++){
				sb1.append(log_sent[i]);
			}
			for (int i=index+2;i<log_sent.length;i++){
				sb2.append(log_sent[i]);
			}
			String str1 = sb1.toString();
			String str2 = sb2.toString();
			String imply = "=>";
			String a = "(", b = ")", c = ")&(";
			String new_str = a + str1 + imply + str2 + c + str2 + imply + str1 + b;
			
			log_sent = new char [new_str.length()];
			log_sent = new_str.toCharArray();
			return log_sent;
		}
		else{
			return log_sent; 
		}
	}
}
