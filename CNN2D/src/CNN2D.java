import java.util.*;
import java.lang.*;
import java.math.*;

public class CNN2D {
	
	static int imdt=0,fcdt=0,no=0,ni=0,lr=0,lc=0,fr=0,fc=0,ur,uc,pl,pt,pb,pr;
	static int ifmz[] = new int[3];
	static int ifzp[] = new int[4];
	static int ifsf[] = new int[2];
	
	static int fcmz[] = new int[4];
	static int fcsf[] = new int[2];
	
	static int ofsf[] = new int[2];
	
	static int inputMatrix[][][];
	static int filterMatrix[][][][];
	static int outputMatrix[][][];

	
	
	public static void dataGeneration(int dataType, int MapSize[]) {
		//input feature maps
		if(MapSize.length > 3) {
			
			no = MapSize[0];
			ni = MapSize[1];
			fr = MapSize[2];
			fc = MapSize[3];

			filterMatrix = new int [no][ni][fr][fc];
			
			int count = -1;
			for(int a=0;a<no;a++) {
				for(int b=0;b<ni;b++) {
					for(int i=0;i<fr;i++) {
						for(int j=0;j<fc;j++) {
							if(dataType == 1) {
								filterMatrix[a][b][i][j] = (int) (Math.random()*10); 
							}else {
								count +=1;
								filterMatrix[a][b][i][j] = count;
								
							}
						}
					}
				}
			}
			
			
		}else {
			ni = MapSize[0];
			lr = MapSize[1];
			lc = MapSize[2];
			
			inputMatrix = new int [ni][lr][lc];
			
			int count = -1;
			for(int k=0;k<ni;k++) {
				for(int i=0;i<lr;i++) {
					for(int j=0;j<lc;j++) {
						if(dataType == 1) {
							inputMatrix[k][i][j] = (int) (Math.random()*10); 
						}else {
							count +=1;
							inputMatrix[k][i][j] = count;
						}
						}
					}
			}
			
			
		}
	
	}
	
	public static int[][][] dataPreprocessing(int matrix[][][],int samplingFactor[]) {
		//Up sampling
		
		 ur = samplingFactor[0] + 1;
		 uc = samplingFactor[1] + 1;
		
		lr = ur * matrix[0].length;
		lc = uc * matrix[0][0].length;
		
		int upsampledMatrix[][][] = new int [ni][lr][lc];
		
		for(int a=0;a<ni;a++) {
		for(int i=0;i<matrix[0].length;i++) {
			for(int j=0;j<matrix[0][0].length;j++) {
				try{
					upsampledMatrix[a][i*ur][j*uc] = matrix[a][i][j];
				}catch(Exception e){
					upsampledMatrix[a][i][j] = 0;
				}
			}
		 }
		}
		return upsampledMatrix;
	}
	
	public static int[][][][] dataPreprocessing(int matrix[][][][],int samplingFactor[]) {
		//Up sampling
		
		 ur = samplingFactor[0] + 1;
		 uc = samplingFactor[1] + 1;
		
		fr = ur * matrix[0][0].length;
		fc = uc * matrix[0][0][0].length;
		
		int upsampledMatrix[][][][] = new int [no][ni][fr][fc];
		
		for(int a=0;a<no;a++) {
		for(int b=0;b<ni;b++) {
		for(int i=0;i<matrix[0][0].length;i++) {
			for(int j=0;j<matrix[0][0][0].length;j++) {
				try{
					upsampledMatrix[a][b][i*ur][j*uc] = matrix[a][b][i][j];
				}catch(Exception e){
					upsampledMatrix[a][b][i][j] = 0;
				}
			}
		}
		}
		}
		return upsampledMatrix;
	}
	
	
	public static int[][][] zeroPadding(int matrix[][][],int paddingMatrix[]){
		 pl = paddingMatrix[0];
		 pr = paddingMatrix[1];
		 pt = paddingMatrix[2];
		 pb = paddingMatrix[3];
		
		lr = pl + matrix[0].length + pr ;
		lc = pt + matrix[0][0].length + pb;
		
		int zeroPaddedMatrix[][][] = new int [ni][lr][lc];
		
		
		for(int a=0;a<matrix.length;a++) {
		for(int i=0;i<matrix[0].length; i++) {
			for(int j=0;j<matrix[0][0].length;j++) {
				try{
					zeroPaddedMatrix[a][i+pl][j+pt] = matrix[a][i][j];
				}catch(Exception e){
					zeroPaddedMatrix[a][i][j] = 0;
				}
			}
		}
		}
		return zeroPaddedMatrix;
		
	}
	
	
	public static int[][][] matrixMultiplication(int input[][][],int filter[][][][]) {
		
		int resultMatrix[][][] = new int [no][lr-fr+1][lc-fc+1];
		
		for(int a=0;a<= no-1;a++) {
			for(int mr=0;mr <= lr-fr;mr++) {
				for(int mc=0;mc<=lc-fc; mc++) {
					for(int i=0;i<=ni-1;i++) {
						for(int j=0;j<fr;j++) {
							for(int k=0;k<fc;k++) {
								resultMatrix[a][mr][mc] += filter[a][i][j][k]*input[i][mr+j][mc+k];
							}
						}
					}
				}
			}
		}
		
		return resultMatrix;
	}
	
	
	public static int[][][] dataPostprocessing(int matrix[][][], int downsampleMap[]) {
		int Mr = matrix[0].length -pt - pb;
		int Mc = matrix[0][0].length -pl-pr;
		int Sr = downsampleMap[0];
		int Sc = downsampleMap[1];
		if(Sr == 0) {
			Sr = 1;
		}
		if(Sc == 0) {
			Sc = 1;
		}
		
		int resultMatrix[][][] = new int [no][Mr/Sr][Mc/Sc];
		for(int a =0;a<no;a++) {
			for(int b=0;b<Mr/Sr;b++) {
				for(int c=0;c<Mc/Sc;c++) {
					resultMatrix[a][b][c] = matrix[a][b*Sr][c*Sc];
				}
			}
		}
		
		return resultMatrix;
	}
	
	public static void dataVisualization(int arr[][][]) {
		System.out.print("[");
		for(int a=0; a<arr.length;a++) {
		for(int i=0;i<arr[0].length;i++) {
			for(int j=0;j<arr[0][0].length;j++) {
				System.out.print(arr[a][i][j]+"\t");
			}
			System.out.print("\n");
		}
		}
		System.out.print("]\n");
	}

	public static void dataVisualization(int arr[][][][]) {
		System.out.print("[");
		for(int a=0; a<arr.length;a++) {
		for(int b=0;b<arr[0].length;b++) {
		for(int i=0;i<arr[0][0].length;i++) {
			for(int j=0;j<arr[0][0][0].length;j++) {
				System.out.print(arr[a][b][i][j]+"\t");
			}
			System.out.print("\n");
			}
			}
		}
		System.out.print("]\n");
	}
	
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		
		System.out.println("Welcome to CNN style 2D convolution\n");
		
		//*********************************
		System.out.println("**** Accepting input coefficients ****\n");
		
		System.out.println("Input feature map data type: 1)random 2)sequential\n");
		imdt = s.nextInt();
		System.out.println("Input feature map size\n");
		System.out.print("[ ni lr lc ]: ");
		for(int i=0;i<3;i++) {ifmz[i] = s.nextInt();}
		
		dataGeneration(imdt,ifmz);
		dataVisualization(inputMatrix);
		
		System.out.println("Input feature up sampling factor\n");
		System.out.print("[ Ur Uc] ");
		for(int i=0;i<2;i++) {ifsf[i] = s.nextInt();}
		
		inputMatrix = dataPreprocessing(inputMatrix,ifsf);
		dataVisualization(inputMatrix);
		
		System.out.println("Input feature zero padding\n");
		System.out.print("[ pl pr pt pb]");
		for(int i=0;i<4;i++) {ifzp[i] = s.nextInt();}
		
		inputMatrix = zeroPadding(inputMatrix,ifzp);
		dataVisualization(inputMatrix);
		
		//*********************************
		
		System.out.println("**** Accepting filter coefficients ****\n");
		System.out.println("Filter coefficient data type: 1)random 2)sequential\n");
		fcdt = s.nextInt();
		System.out.println("Filter coefficient size\n");
		System.out.println("[No Ni Fr Fc]");
		for(int i=0;i<4;i++) {fcmz[i] = s.nextInt();}
		
		dataGeneration(fcdt,fcmz);
		dataVisualization(filterMatrix);
		
		System.out.println("Filter coefficient up sampling factor\n");
		System.out.print("[ Dr Dc] ");
		for(int i=0;i<2;i++) {fcsf[i] = s.nextInt();}
		
		filterMatrix = dataPreprocessing(filterMatrix,fcsf);
		dataVisualization(filterMatrix);
		
		//*********************************
		
		System.out.println("**** Accepting output coefficients ****\n");
		System.out.println("Output feature map channel\n");
		System.out.println("[n0]: ");
		no = s.nextInt();
		
		outputMatrix = matrixMultiplication(inputMatrix,filterMatrix);
		dataVisualization(outputMatrix);
		System.out.println("Output feature down sampling factor\n");
		System.out.print("[ Sr Sc] ");
		for(int i=0;i<2;i++) {ofsf[i] = s.nextInt();}
		outputMatrix = dataPostprocessing(outputMatrix,ofsf);
		dataVisualization(outputMatrix);
		s.close();
	}

}
