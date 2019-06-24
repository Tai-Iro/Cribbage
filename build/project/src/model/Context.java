package model;

import java.util.Comparator;

public class Context {
	private final static Context instance = new Context();

	private Comparator<Integer> c = new Comparator<Integer>()
    {
		public int compare(Integer i1, Integer i2) {
			int j = i1%13;
			int k = i2%13;
			if(Integer.compare(j, k) == 0){
				return i1.compareTo(i2);
			}
			else
				return Integer.compare(j, k);
		}
    };
    
	public static Context getInstance() {
        return instance;
    }
	
	public Comparator<Integer> getC(){
		return c;
	}
}
