package edu.wsu.KheperaSimulator;

public class sortObject implements Comparable{
protected Integer dist;
protected int windex;

public sortObject(Integer a,int b)
{
	this.dist = a;
	this.windex = b;
}

public int compareTo(Object o){
	int result = 0;
	sortObject temp = (sortObject) o;
	result = this.dist.compareTo(temp.dist);
	return result;
}
}
