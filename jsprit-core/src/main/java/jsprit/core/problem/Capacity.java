package jsprit.core.problem;

/**
 * Capacity with an arbitrary number of capacity-dimension.
 * 
 * <p>Note that this assumes the the values of each capacity dimension can be added up and subtracted
 * 
 * @author schroeder
 *
 */
public class Capacity {

	/**
	 * Adds up two capacities, i.e. sums up each and every capacity dimension, and returns the resulting Capacity.
	 * 
	 * <p>Note that this assumes that capacity dimension can be added up.
	 * 
	 * @param cap1
	 * @param cap2
	 * @return new capacity
	 * @throws NullPointerException if one of the args is null

	 */
	public static Capacity addup(Capacity cap1, Capacity cap2){
		if(cap1==null || cap2==null) throw new NullPointerException("arguments must not be null");
		Capacity.Builder capacityBuilder= Capacity.Builder.newInstance();
		for(int i=0;i<Math.max(cap1.getNuOfDimensions(),cap2.getNuOfDimensions());i++){
			capacityBuilder.addDimension(i, cap1.get(i)+cap2.get(i));
		}
		return capacityBuilder.build();
	}
	
	/**
	 * Subtracts cap2substract from cap and returns the resulting Capacity.
	 * 
	 * @param cap
	 * @param cap2substract
	 * @return new capacity
	 * @throws NullPointerException if one of the args is null
	 * @throws IllegalStateException if number of capacityDimensions of cap1 and cap2 are different (i.e. <code>cap1.getNuOfDimension() != cap2.getNuOfDimension()</code>).
	 * @throws IllegalStateException if one of the capacityDimenstions has a negative value after subtracting 
	 */
	public static Capacity subtract(Capacity cap, Capacity cap2substract){
		if(cap==null || cap2substract==null) throw new NullPointerException("arguments must not be null");
		Capacity.Builder capacityBuilder= Capacity.Builder.newInstance();
		for(int i=0;i<Math.max(cap.getNuOfDimensions(),cap2substract.getNuOfDimensions());i++){
			int dimValue = cap.get(i)-cap2substract.get(i);
			if(dimValue<0) throw new IllegalStateException("this must not be. dimension " + i + " has a negative value after subtracting");
			capacityBuilder.addDimension(i, dimValue);
		}
		return capacityBuilder.build();
	}
	
	
	
	/**
	 * Makes a deep copy of Capacity.
	 * 
	 * @param capacity
	 * @return
	 */
	public static Capacity copyOf(Capacity capacity){
		if(capacity == null) return null;
		return new Capacity(capacity);
	}
	
	/**
	 * Builder that builds Capacity
	 * 
	 * @author schroeder
	 *
	 */
	public static class Builder {
		
		/**
		 * default is 1 dimension with size of zero
		 */
		private int[] dimensions = new int[1];
		
		/**
		 * Returns a new instance of Capacity with one dimension and a value/size of 0
		 * 
		 * @return this builder
		 */
		public static Builder newInstance(){
			return new Builder();
		}
		
		Builder(){}
		
		/**
		 * add capacity dimension
		 * 
		 * <p>Note that it automatically resizes dimensions according to index, i.e. if index=7 there are 8 dimensions.
		 * New dimensions then are initialized with 0
		 * 
		 * @throw IllegalStateException if dimValue < 0
		 * @param index
		 * @param dimValue
		 * @return
		 */
		public Builder addDimension(int index, int dimValue){
			if(dimValue<0) throw new IllegalStateException("dimValue can never be negative");
			if(index < dimensions.length){
				dimensions[index] = dimValue;
			}
			else{
				int requiredSize = index + 1;
				int[] newDimensions = new int[requiredSize]; 
				copy(dimensions,newDimensions);
				newDimensions[index]=dimValue;
				this.dimensions=newDimensions;
			}
			return this;
		}
		
		private void copy(int[] from, int[] to) {
			for(int i=0;i<dimensions.length;i++){
				to[i]=from[i];
			}
		}

		/**
		 * Builds an immutable Capacity and returns it.
		 * 
		 * @return Capacity
		 */
		public Capacity build() {
			return new Capacity(this);
		}

		
	}
	
	private int[] dimensions;
	
	/**
	 * copy constructor
	 * 
	 * @param capacity
	 */
	Capacity(Capacity capacity){
		this.dimensions = new int[capacity.getNuOfDimensions()];
		for(int i=0;i<capacity.getNuOfDimensions();i++){
			this.dimensions[i]=capacity.get(i);
		}
	}
	
	Capacity(Builder builder) {
		dimensions = builder.dimensions;
	}

	/**
	 * Returns the number of specified capacity dimensions.
	 * 
	 * @return
	 */
	public int getNuOfDimensions(){
		return dimensions.length;
	}
	
	/**
	 * Returns value of capacity-dimension with specified index.
	 * 
	 * <p>If capacity dimension does not exist, it returns 0 (rather than IndexOutOfBoundsException).
	 * 
	 * @param index
	 * @return
	 */
	public int get(int index){
		if(index<dimensions.length) return dimensions[index];
		return 0;
	}

	/**
	 * Returns true if this capacity is less or equal than the capacity toCompare, i.e. if none of the capacity dimensions > than the corresponding dimension in toCompare.
	 * 
	 * @param toCompare
	 * @return
	 * @throws NullPointerException if one of the args is null
	 * @throws IllegalStateException if number of capacityDimensions of this capacity and toCompare are different.
	 */
	public boolean isLessOrEqual(Capacity toCompare){
		if(toCompare == null) throw new NullPointerException();
		if(this.getNuOfDimensions() != toCompare.getNuOfDimensions()) throw new IllegalStateException("cap1.getNuOfDimension()="+this.getNuOfDimensions()+
				"!= cap2.getNuOfDimension()="+toCompare.getNuOfDimensions()+ ". cannot add up capacities with different dimension.");
		for(int i=0;i<this.getNuOfDimensions();i++){
			if(this.get(i) > toCompare.get(i)) return false;
		}
		return true;
	}
	
}