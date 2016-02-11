package com.tustanovskyy.model;

import java.util.Comparator;

public class PairResult implements Comparator<PairResult>, Comparable<PairResult> {

	private Double sigma;

	private Double MAPE;

	public Double getMAPE() {
		return MAPE;
	}

	public PairResult() {
		// TODO Auto-generated constructor stub
	}

	public PairResult(Double sigma, Double MAPE) {
		this.MAPE = MAPE;
		this.sigma = sigma;
	}

	public void setMAPE(Double mAPE) {
		MAPE = mAPE;
	}

	public Double getSigma() {
		return sigma;
	}

	public void setSigma(Double sigma) {
		this.sigma = sigma;
	}

	public int compare(PairResult o1, PairResult o2) {
		return (int) (o1.MAPE - o2.MAPE);
	}

	public int compareTo(PairResult o) {
		
		return this.MAPE.compareTo(o.MAPE);
	}

}
