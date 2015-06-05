package model;

import java.io.IOException;

import reader.ConfigReader;
import reader.DataProcessor;
import entity.AverageEntity;
import entity.Record;
import entity.RecordContainer;

public class UserBased {
	//
	
	float threshold = 0.5f;
	int userNum = 0;
	int movieNum = 0;
	public float[][] scoreMatrix;
	float[][] similarityMatrix;
	
	public void loadData(RecordContainer train) {
		RecordContainer data = train;
		userNum = data.getUserNum();
		movieNum = data.getMovieNum();
		
		System.out.println("coming");
		
		scoreMatrix = new float[userNum][movieNum];
		
		System.out.println("out");
		
		//Initialize the scoreMatrix
		for (int i = 0; i < scoreMatrix.length; i++) {
			for (int j = 0; j < scoreMatrix[0].length; j++) {
				scoreMatrix[i][j] = 0.0f;
			}
		}
		Record u = null;
		while ((u = data.getNext()) != null) {
			scoreMatrix[u.getId()][u.getMovieId()] = u.getScore();
		}
		System.out.println("Finished initializing the score matrix");
	}
	
	public void similarityMatrix() {
		//Build the similarity matrix
		
		System.out.println(userNum);
		similarityMatrix = new float[userNum][userNum]; 
		System.out.println(userNum);
		
		float[] vec1;
		float[] vec2;
		float[] tempVec1;
		float[] tempVec2;
		float tempSimilarity = 0.0f;
		int index;
		vec1 = new float[movieNum];//movieNum?
		vec2 = new float[movieNum];
		//Initialize the vec1 and vec2
		for (int i = 0; i < vec1.length; i++) {
			vec1[i] = 0.0f;
			vec2[i] = 0.0f;
		}
		//Start to build similarity matirx
		int testCounter1 = 0; //test
		int testCounter2 = 0; //test
		
		for (int i = 0; i < scoreMatrix.length; i++) {//scoreMatrix.length or scoreMatrix[0].length?
			for (int j = 0; j < scoreMatrix.length; j++) {
				tempVec1 = getRow(i);
				tempVec2 = getRow(j);
				index = 0;
				//Remove the element 0 either in tempVec1 or tempVec2
				for (int k = 0; k < tempVec1.length; k++) {
					if (tempVec1[k] != 0.0f && tempVec2[k] != 0.0f) {
						vec1[index] = tempVec1[k];
						vec2[index] = tempVec2[k];
						index++;
					}
				}
				tempSimilarity = cosineSimilarity(vec1, vec2);
				similarityMatrix[i][j] = tempSimilarity;
				System.out.println("In 2nd for loop" + testCounter1);
				testCounter1 ++; //test
			}
			System.out.println("In 1st for loop" + testCounter2);
		}
		System.out.println("Finished the similarityMatrix");
		
	}
	
	public float[] getRow(int x) {
		//Get the xth row from the scoreMatrix
		float[] targetRow;
		targetRow = new float[movieNum];
		
		for (int i = 0; i < targetRow.length; i++) {
			targetRow[i] = 0.0f;
		}
		for (int i = 0; i < scoreMatrix[0].length; i++) {//scoreMatrix[0].length or scoreMatrix.length?
			targetRow[i] = scoreMatrix[x][i];
		}
		return targetRow;
	}
	
	public float cosineSimilarity(float x[], float y[]) {
		//Compute the distance of two user by using cosineSimilarty
		double dotProduct = 0.0;
		double magnitude1 = 0.0;
		double magnitude2 = 0.0;
		double cosineSimilarity = 0.0;
		for (int i = 0; i < x.length; i++) {
			dotProduct += (x[i] * y[i]);
			magnitude1 += Math.pow(x[i], 2);
			magnitude2 += Math.pow(y[i], 2);
		}
		magnitude1 = Math.sqrt(magnitude1);
		magnitude2 = Math.sqrt(magnitude2);
		cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
		return (float)cosineSimilarity;
	}
	
	public void predict() {
		//Predict the missing score in scoreMatrix
		float weightedScoreSum = 0.0f;
		float counter = 0.0f;
		
		for (int i = 0; i < scoreMatrix.length; i++) {
			for (int j = 0; j < scoreMatrix[0].length; j++) {
				if (scoreMatrix[i][j] == 0.0f) {
					for (int k = 0; k < similarityMatrix[0].length; k++) {//or similarityMatrix.length?
						if ((similarityMatrix[j][k] > threshold) && (scoreMatrix[k][i] != 0.0f)) {//Why it is not equal to 0?
							weightedScoreSum += similarityMatrix[j][k] * scoreMatrix[i][k]; //Similarity() * known score
							counter += similarityMatrix[j][k];
						}
					}
					scoreMatrix[i][j] = weightedScoreSum / counter;
					weightedScoreSum = 0.0f;
					counter = 0.0f;
				}
			}
		}
	}
}
