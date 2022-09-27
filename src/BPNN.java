package predict;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.IterativeClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class BPNN extends AbstractClassifier implements IterativeClassifier {
	private static final long serialVersionUID = -416172372730928306L;
	private boolean resume = false;
	/**��J�h | ��X�h�Ӽ� | ���üh�Ӽ� | ����`�Ӽ�(inputNum+outputNum) | ���N����*/
	private int inputNum = 0, outputNum = 0, hiddenNum = 10, totalNum = 0;
	/**�w����j�馸�� | �`�j�馸��*/
	private int numOfPerform = 0, epoch = 1000;
	/**�ǲ߲v*/
	private double eta = 0.5, alpha = 0;
	/** �����U�`�I��X�� */
	private double[] X, T, H, Y;
	/** �������V��W���v���� */
	private double[][] W_xh, W_hy;
	/** �������V��W���v�����ܤƶq */
	private double[][] dW_xh, dW_hy;
	/** �������üh�P��X�h�`�I���v�� */
	private double[] Q_h, Q_y;
	/** �������üh�P��X�h�`�I���v���ܤƶq */
	private double[] dQ_h, dQ_y;
	/** �������üh�P��X�h�`�I��X���ܤƶq */
	private double[] delta_h, delta_y;
	
	private double c1 = 1, c2 = 1, r1 = 0.0, r2 = 0.0,w = 1, w0 = w, w1 = 0.7;	//�ۤv�¦�}�B�F�~��}�B�D���v��
	private int numParticle = 20, maxgen1, cnt = 0, tnt = 0;	//�ڸs�j�p
	private double[][] xp ;		//�ɤl��m
	private double[][] v ;		//�ɤl�t�v
	private double[][] Pbest;	//�L�h�o�{�̨Φ�m
	private double[][] Gbest;	//����̨Φ�m
	private double[] xlo;		//�̤p����
	private double[] xhi;		//�̤j����
	private PSOparameter Pg;
	private ArrayList<PSOparameter> mPP = new ArrayList<>();		//�ɤl�s��m
	private ArrayList<PSOparameter> mPV = new ArrayList<>();		//�ɤl�s�t�v
	private HashMap<Integer, PSOparameter> Pb = new HashMap<>();    //�@���ɤl���N���X�{�̦n����
	private double mse = 0.0, oldmae = 0.0,mae = 0.0;
	private BigDecimal bd;
	private boolean check = false;
	
	/**�]�w�ǲ߲v*/
	public void setLearningRate(double l) {
		if (l > 0 && l <= 1) {
			eta = l;
	    }else {
	    	throw new IllegalArgumentException("����>0��<=1");
	    }
	}
	/**�o��ǲ߲v*/
	public double getLearningRate() {
		return eta;
	}
	
	/**�]�w�D�ʦ]�l*/
	public void setAlpha(double m) {
		if (m >= 0 && m <= 1) {
			alpha = m;
		}else {
			throw new IllegalArgumentException("����>=0��<=1");
		}
	}
	/**�o��D�ʦ]�l*/
	public double getAlpha() {
		return alpha;
	}
	
	/**�]�w���üh*/
	public void setHiddenLayers(int h) {
		if(h > 0) {
			hiddenNum = h;
		}else {
			throw new IllegalArgumentException("����>0");
		}
	}
	/**�o�����üh*/
	public int getHiddenLayers() {
		return hiddenNum;
	}	
	
	/**�]�w�V�m����*/
	public void setTrainingTime(int n) {
		if(n > 0) {
			epoch = n;
		}else {
			throw new IllegalArgumentException("����>0");
		}
	}
	/**�o��V�m����*/
	public int getTrainingTime() {
		return epoch;
	}
	
	/**�ŧi�}�C �N�}�C��J��l��*/
	private void initialize(int inputNum, int hiddenNum, int outputNum) {
		X = new double[inputNum];
		H = new double[hiddenNum];
		Y = new double[outputNum];
		T = new double[outputNum];
		W_xh = new double[inputNum][hiddenNum];
		W_hy = new double[hiddenNum][outputNum];
		dW_xh = new double[inputNum][hiddenNum];
		dW_hy = new double[hiddenNum][outputNum];
		Q_h = new double[hiddenNum];
		Q_y = new double[outputNum];
		dQ_h = new double[hiddenNum];
		dQ_y = new double[outputNum];
		delta_h = new double[hiddenNum];
		delta_y = new double[outputNum];
		
		xlo = new double[numParticle];
		xhi = new double[numParticle];
		xp = new double[totalNum + 2][hiddenNum];
		v = new double[totalNum + 2][hiddenNum];
		Pbest = new double[totalNum + 2][hiddenNum];
		Gbest = new double[totalNum + 2][hiddenNum];
		maxgen1 = epoch * 3 / 5;
		
		for (int h = 0; h < hiddenNum; h++)
			for (int i = 0; i < inputNum; i++) {
				dW_xh[i][h] = 0.0;
			}
		for (int j = 0; j < outputNum; j++)
			for (int h = 0; h < hiddenNum; h++) {
				dW_hy[h][j] = 0.0;
			}
		for (int h = 0; h < hiddenNum; h++) {
			dQ_h[h] = 0.0;
			delta_h[h] = 0.0;
		}
		for (int j = 0; j < outputNum; j++) {
			dQ_y[j] = 0.0;
			delta_y[j] = 0.0;
		}
	}
	
	private void initialize_P() {
		for(int num = 0; num < numParticle; num++) {
			W_xh = new double[inputNum][hiddenNum];
			W_hy = new double[hiddenNum][outputNum];
			Q_h = new double[hiddenNum];
			Q_y = new double[outputNum];
			
			for (int h = 0; h < hiddenNum; h++) {
				Q_h[h] = Math.random();
				for (int i = 0; i < inputNum; i++) 
					W_xh[i][h] = Math.random();
			}

			for (int j = 0; j < outputNum; j++) {
				Q_y[j] = Math.random();
				for (int h = 0; h < hiddenNum; h++) 
					W_hy[h][j] = Math.random();
			}
			next();
			PSOparameter PP = new PSOparameter(W_xh, W_hy,Q_h,Q_y, mae);
			mPP.add(PP);
			
			//�̤j/�̤p�t�v�]�w
			xlo[num] = -5;
			xhi[num] = 5;
		}
	}

	private void Initialize_V() {		
		for(int num = 0; num < numParticle; num++) {
			W_xh = new double[inputNum][hiddenNum];
			W_hy = new double[hiddenNum][outputNum];
			Q_h = new double[hiddenNum];
			Q_y = new double[outputNum];
			
			for (int h = 0; h < hiddenNum; h++) {
				Q_h[h] = Math.random();
				for (int i = 0; i < inputNum; i++) 
					W_xh[i][h] = Math.random();
			}
			for (int j = 0; j < outputNum; j++) {
				Q_y[j] = Math.random();
				for (int h = 0; h < hiddenNum; h++) 
					W_hy[h][j] = Math.random();
			}
			
			PSOparameter PP = new PSOparameter(W_xh, W_hy,Q_h,Q_y);
			mPV.add(PP);
		}
	}
	
	/**��l�Ƶ{��*/
	Instances instances;
	@Override
	public void initializeClassifier(Instances data) throws Exception {
		numOfPerform = 0;
		
		getCapabilities().testWithFail(data); // �������O�_�i�H�B�z���
		Instances insts = new Instances(data);
		insts.deleteWithMissingClass();	//�R���㦳�ʥ������O�����
		
		instances = new Instances(insts);
		totalNum = instances.numAttributes();
		outputNum = instances.numClasses();
		inputNum = totalNum - outputNum;
		
		initialize(inputNum, hiddenNum, outputNum);
		initialize_P();
		Initialize_V();
		for (int i = 0; i < numParticle; i++) {		//�D��̦n���ɤl�A��̤p���A�s��
			Pb.put(i, mPP.get(i));
		}
	    Pg = Pb.get(0);
	    for (int i = 1; i < numParticle; i++) {
	    	if (Pg.getFitness() > Pb.get(i).getFitness()) {
	    		Pg = Pb.get(i);
	        }
	    }
	    oldmae = Pg.getFitness();	//�����ثe���̨ξA�s��
	}
	
	/**���毫�g�����e�V�Ǽ��k*/
	@Override
	public boolean next()  {		
		double sum = 0.0;
		mse = 0.0;
		mae = 0.0;
		for(Instance instance: instances) {	// �M���Ҧ���ƶ����e
			for (int i = 0; i < inputNum; i++) {
				X[i] = instance.value(i);
			}
			for (int i = inputNum; i < totalNum; i++) {
				T[i - inputNum] = instance.value(i);
			}
			for (int h = 0; h < hiddenNum; h++) {
				sum = 0.0;
				for (int i = 0; i < inputNum; i++)
					sum += X[i] * W_xh[i][h];
				H[h] = (float) 1.0 / (1.0 + Math.exp(-(sum - Q_h[h])));
			}
			for (int j = 0; j < outputNum; j++) {
				sum = 0.0;
				for (int h = 0; h < hiddenNum; h++)
					sum += H[h] * W_hy[h][j];
				Y[j] = (float) 1.0 / (1.0 + Math.exp(-(sum - Q_y[j])));
			}
			for (int j = 0; j < outputNum; j++) {				
				mse += (T[j] - Y[j]) * (T[j] - Y[j]);
				mae += Math.abs(T[j] - Y[j]);
			}
		}
		mse = mse / instances.size(); // mse
		mae = mae / instances.size(); // mae
		return true;
	}
	
	/**���毫�g����*/
	private void BPNN_method() {
		double sum = 0.0;
		mse = 0.0;
		mae = 0.0;
		for(Instance instance: instances) {	// �M���Ҧ���ƶ����e
			for (int i = 0; i < inputNum; i++) {
				X[i] = instance.value(i);
			}
			for (int i = inputNum; i < totalNum; i++) {
				T[i - inputNum] = instance.value(i);
			}
			for (int h = 0; h < hiddenNum; h++) {
				sum = 0.0;
				for (int i = 0; i < inputNum; i++)
					sum += X[i] * W_xh[i][h];
				H[h] = (float) 1.0 / (1.0 + Math.exp(-(sum - Q_h[h])));
			}
			for (int j = 0; j < outputNum; j++) {
				sum = 0.0;
				for (int h = 0; h < hiddenNum; h++)
					sum += H[h] * W_hy[h][j];
				Y[j] = (float) 1.0 / (1.0 + Math.exp(-(sum - Q_y[j])));
			}
			//Backpropagation
			for (int j = 0; j < outputNum; j++)
				delta_y[j] = Y[j] * (1.0 - Y[j]) * (T[j] - Y[j]);
			for (int h = 0; h < hiddenNum; h++) {
				sum = 0.0;
				for (int j = 0; j < outputNum; j++)
					sum += W_hy[h][j] * delta_y[j];
				delta_h[h] = H[h] * (1.0 - H[h]) * sum;
			}
			for (int j = 0; j < outputNum; j++)
				for (int h = 0; h < hiddenNum; h++)
					dW_hy[h][j] = eta * delta_y[j] * H[h] + alpha * dW_hy[h][j];
	
			for (int j = 0; j < outputNum; j++)
				dQ_y[j] = -eta * delta_y[j] + alpha * dQ_y[j];
	
			for (int h = 0; h < hiddenNum; h++)
				for (int i = 0; i < inputNum; i++)
					dW_xh[i][h] = eta * delta_h[h] * X[i] + alpha * dW_xh[i][h];
	
			for (int h = 0; h < hiddenNum; h++)
				dQ_h[h] = -eta * delta_h[h] + alpha * dQ_h[h];
	
			for (int j = 0; j < outputNum; j++)
				for (int h = 0; h < hiddenNum; h++)
					W_hy[h][j] = W_hy[h][j] + dW_hy[h][j];
	
			for (int j = 0; j < outputNum; j++)
				Q_y[j] = Q_y[j] + dQ_y[j];
	
			for (int h = 0; h < hiddenNum; h++)
				for (int i = 0; i < inputNum; i++)
					W_xh[i][h] = W_xh[i][h] + dW_xh[i][h];
	
			for (int h = 0; h < hiddenNum; h++)
				Q_h[h] = Q_h[h] + dQ_h[h];
			
			//�p��mse(�֥[)
			for (int j = 0; j < outputNum; j++) {				
				mse += (T[j] - Y[j]) * (T[j] - Y[j]);
				mae += Math.abs(T[j] - Y[j]);
			}
		}
		mse = mse / instances.size(); // mse
		mae = mae / instances.size(); // mae
	}
	
	/**����PSO�B�z*/
	PSOparameter PP;
	private void evulation() {
		int gen = numOfPerform + 1;
		
		for(int i=0; i<numParticle; i++) {			//��s�C���ɤl���t�v�P��m
		   	r1 = Math.random(); r2 = Math.random();
		   	
		   	v = mPV.get(i).getParticleVelocity();
		   	xp = mPP.get(i).getParticlePosition();
		   	Pbest = Pb.get(i).getParticlePosition();
		   	Gbest = Pg.getParticlePosition();
		   	
		   	for(int k=0; k<totalNum + 2; k++) {
		   		for(int j=0; j<hiddenNum; j++) {
		   			v[k][j] =  w * v[k][j] + c1 * r1 * (Pbest[k][j] - xp[k][j]) + c2 * r2 * (Gbest[k][j] - xp[k][j]);
		   			
			        if(v[k][j] > xhi[i]) v[k][j] = xhi[i];		//�V�ɳB�z
			        if(v[k][j] < xlo[i]) v[k][j] = xlo[i];
			       
			        xp[k][j] = xp[k][j] + v[k][j];			        
			        if(xp[k][j] > xhi[i]) xp[k][j] = xhi[i];	//�V�ɳB�z
			        if(xp[k][j] < xlo[i]) xp[k][j] = xlo[i];
			        		        
			        if(k == totalNum + 1) break; 	//��Q_y�N�n���X�F�A�]���u���@�ӭ�
		   		}
		   	}
		   
		   	saveParameter(xp);	//��m�s�^����A���s�����A�s��
			next();
			mPP.remove(i);		//�R�����windex������
	        PP = new PSOparameter(W_xh, W_hy,Q_h,Q_y, mae);	//��s�ɤl��}
	        mPP.add(i,PP);
	        
	        saveParameter(v);	//�t�v�s�^
			mPV.remove(i);		//�R�����windex������
	        PP = new PSOparameter(W_xh, W_hy,Q_h,Q_y);		//��s�ɤl�t�v
	        mPV.add(i,PP);
	    }
		for(int num = 0; num < numParticle; num++) {	
			if (Pb.get(num).getFitness() > mPP.get(num).getFitness()) {	//�D��̦n������
                Pb.replace(num, mPP.get(num));
            }
            if (Pg.getFitness() > Pb.get(num).getFitness()) {
                Pg = Pb.get(num);
            }
		}
		// Optimization
		if(1 <= gen && gen <= maxgen1) {	//�۾A���D���v��	
			w = w0 - (w1 / maxgen1) * gen;
		}else {
			w =  (w0 - w1) * Math.exp((maxgen1 - gen)/1300.0);
		}		
	}
	
	@Override
	public void done() throws Exception {
		
	}
	
	/**�}�l�إ߼ҫ����{��*/
	@Override
	public void buildClassifier(Instances data) throws Exception {
	    instances = null;

	    // Initialize classifier
	    initializeClassifier(data);
	    
	    // For the given number of iterations
	    while (numOfPerform != epoch ) {
	    	
	    	if(!check) {	//check �@�}�l��false�A�]���|������PSO
	    		
	    		evulation();	//����PSO�B�z
	    		mae = Pg.getFitness();				//�x�s�̨βɤl���A�s��
	    		
	    	}else {	//�Y���N500�����S��s�A�h�ϥ�BPNN_method
	    		tnt++;
	    		if(tnt > 200) break;
	    		
	    		BPNN_method();	//�����V�Ǽ���k
	    	}
	    		    	
	    	bd = new BigDecimal(mae).setScale(6, RoundingMode.HALF_UP);
	    	
	    	if(oldmae == bd.doubleValue()) {	//�P�w�Y�S��s�A����1��
	    		cnt++;
	    		if(cnt >= 500) {		//�P�w�O�_��F500���S��s
	    			check = true;	
	    			cnt = 0;
	    			Gbest = Pg.getParticlePosition();
		    		saveParameter(Gbest);
	    			System.out.println("y" + "�A�b�� " + numOfPerform + "�����N�ഫ�t��k!!" + "\n");
	    			for (int h = 0; h < hiddenNum; h++) {			//��X���g���������e
	    				for (int k = 0; k < inputNum; k++) {
	    					System.out.printf(W_xh[k][h] + " ");	
	    				}
	    				System.out.println();
	    			}
	    			for (int j = 0; j < outputNum; j++) {
	    				for (int h = 0; h < hiddenNum; h++) {
	    					System.out.printf(W_hy[h][j] + " ");
	    				}
	    				System.out.println();
	    			}
	    			for(int j = 0; j < hiddenNum; j++) {
	    				System.out.printf(Q_h[j] + " ");
	    			}
	    			System.out.println();
	    			for(int j = 0; j < outputNum; j++) {
	    				System.out.printf(Q_y[j] + " ");
	    			}
	    			System.out.println();
	    		}
	    	}else {
	    		oldmae = bd.doubleValue();		//�Y�P�w����s�A���s�p�ơA�è��N���eMSE
	    		cnt = 0;
	    	}
	    	
	    	if ((numOfPerform % 100) == 0) { 	// �C�@��(���N100��)��X�@��rmse
//				System.out.printf("Icycle=%4d rmse=%-8.6f\n", numOfPerform, Math.sqrt(mse));
				System.out.printf("Icycle=%4d mae=%-8.6f\n\n", numOfPerform, mae);
			}
	    
	    	numOfPerform++;
	    }	    
	    	    
	    for (int h = 0; h < hiddenNum; h++) {			//��X���g���������e
			for (int k = 0; k < inputNum; k++) {
				System.out.printf(W_xh[k][h] + " ");	
			}
			System.out.println();
		}
		for (int j = 0; j < outputNum; j++) {
			for (int h = 0; h < hiddenNum; h++) {
				System.out.printf(W_hy[h][j] + " ");
			}
			System.out.println();
		}
		for(int j = 0; j < hiddenNum; j++) {
			System.out.printf(Q_h[j] + " ");
		}
		System.out.println();
		for(int j = 0; j < outputNum; j++) {
			System.out.printf(Q_y[j] + " ");
		}
		System.out.println();
		
	    done();	// Clean up
	}
	
	/**�x�s�ɤl���e*/
	public void saveParameter(double[][] m) {
		W_xh = new double[inputNum][hiddenNum];
		W_hy = new double[hiddenNum][outputNum];
		Q_h = new double[hiddenNum];
		Q_y = new double[outputNum];
		
	  	for (int h = 0; h < hiddenNum; h++) {			
			for (int k = 0; k < inputNum; k++) {
				W_xh[k][h] = m[k][h];
			}
		}
		for (int j = 0; j < outputNum; j++) {
			for (int h = 0; h < hiddenNum; h++) {
				W_hy[h][j] = m[j + inputNum][h];
			}
		}
		for(int j = 0; j < hiddenNum; j++) {
			Q_h[j] = m[totalNum][j];
		}
		for(int j = 0; j < outputNum; j++) {
			Q_y[j] = m[totalNum + 1][j];
		}
	}
	
	/**�x�s�̨βɤl�����e*/
	public void saveBestParameter() {
		W_xh = new double[inputNum][hiddenNum];
		W_hy = new double[hiddenNum][outputNum];
		Q_h = new double[hiddenNum];
		Q_y = new double[outputNum];
		
		Gbest = Pg.getParticlePosition();
	  	for (int h = 0; h < hiddenNum; h++) {			//��X�̨βɤl�����e
			for (int k = 0; k < inputNum; k++) {
				W_xh[k][h] = Gbest[k][h];
				System.out.printf(W_xh[k][h] + " ");	
			}
			System.out.println();
		}
		for (int j = 0; j < outputNum; j++) {
			for (int h = 0; h < hiddenNum; h++) {
				W_hy[h][j] = Gbest[j + inputNum][h];
				System.out.printf(W_hy[h][j] + " ");
			}
			System.out.println();
		}
		for(int j = 0; j < hiddenNum; j++) {
			Q_h[j] = Gbest[totalNum][j];
			System.out.printf(Q_h[j] + " ");
		}
		System.out.println();
		for(int j = 0; j < outputNum; j++) {
			Q_y[j] = Gbest[totalNum + 1][j];
			System.out.printf(Q_y[j] + " ");
		}
		System.out.println();
	}
	
	/**����test�禡�A�C���w���@�����*/
	@Override
 	public double classifyInstance(Instance instance) throws Exception {
		for (int i = 0; i < inputNum; i++) {
			X[i] = instance.value(i);
		}
		
		double sum = 0.0;
		for (int h = 0; h < hiddenNum; h++) {
			sum = 0.0;
			for (int i = 0; i < inputNum; i++)
				sum += X[i] * W_xh[i][h];
			H[h] = (float) 1.0 / (1.0 + Math.exp(-(sum - Q_h[h])));
		}
		
		for (int j = 0; j < outputNum; j++) {
			sum = 0.0;
			for (int h = 0; h < hiddenNum; h++)
				sum += H[h] * W_hy[h][j];
			Y[j] = (float) 1.0 / (1.0 + Math.exp(-(sum - Q_y[j])));
		}
		return Y[0];
	}
	
	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		return new double[] {classifyInstance(instance)};
	}
	
	/**�H�U���P�������ʪ�����*/
	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(10);
		newVector.addElement(new Option(
			      "\tLearning rate for the backpropagation algorithm.\n"
			        + "\t(Value should be between 0 - 1, Default = 0.3).", "L", 1,
			      "-L <learning rate>"));
		newVector.addElement(new Option(
			      "\tMomentum rate for the backpropagation algorithm.\n"
			        + "\t(Value should be between 0 - 1, Default = 0.2).", "M", 1,
			      "-M <momentum>"));
		newVector.addElement(new Option("\tNumber of epochs to train through.\n"
			      + "\t(Default = 500).", "N", 1, "-N <number of epochs>"));
		newVector.addAll(Collections.list(super.listOptions()));
		return newVector.elements();
	}
	
	@Override
	public void setOptions(String[] options) throws Exception {
		String learningString = Utils.getOption('L', options);
	    if (learningString.length() != 0) {
	    	setLearningRate(Double.parseDouble(learningString));
	    } else {
	    	setLearningRate(0.7);
	    }
	    String momentumString = Utils.getOption('M', options);
	    if (momentumString.length() != 0) {
	    	setAlpha(Double.parseDouble(momentumString));
	    } else {
	    	setAlpha(0.2);
	    }
	    String hiddenLayers = Utils.getOption('H', options);
	    if (hiddenLayers.length() != 0) {
	    	setHiddenLayers(Integer.parseInt(hiddenLayers));
	    } else {
	    	setHiddenLayers(30);
	    }
	    String epochsString = Utils.getOption('N', options);
	    if (epochsString.length() != 0) {
	    	setTrainingTime(Integer.parseInt(epochsString));
	    } else {
	    	setTrainingTime(1000);
	    }
		super.setOptions(options);
	}
		
	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		options.add("-L");
		options.add("" + getLearningRate());
		options.add("-M");
		options.add("" + getAlpha());
		options.add("-N");
		options.add("" + getTrainingTime()); 
		options.add("-H");
	    options.add("" + getHiddenLayers());
	    Collections.addAll(options, super.getOptions());
	    return options.toArray(new String[options.size()]);
	}
	
	@Override
	public void setResume(boolean resume) throws Exception {
		this.resume = resume;
	}
	
	@Override
	public boolean getResume() {
		return resume;
	}
	
	/**
	 * This will return a string describing the classifier.
	 * @return The string.
	 */
	public String globalInfo() {
		return "�۬�o���˶ǻ����g����";
	}

	/**
	 * @return a string to describe the learning rate option.
	 */
	public String learningRateTipText() {
		return "�ǲ߲v";
	}

	/**
	 * @return a string to describe the momentum option.
	 */
	public String alphaTipText() {
		return "Momentum applied to the weight updates.";
	}
}
