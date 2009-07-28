/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weka.classifiers.semiSupervisedLearning;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Enumeration;
import weka.classifiers.collective.CollectiveRandomizableClassifier;
import weka.classifiers.functions.SMO;
import weka.core.ExpDistance;
import weka.core.Instance;
import weka.core.InstanceComparator;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.matrix.Matrix;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

/**
 * Semi-Supervised Robust Path Spectral Clustering
 * @author Administrator
 */
public class RobustPathSpectralClustering extends CollectiveRandomizableClassifier implements
        TechnicalInformationHandler {

    private int m_filterType;
    private RobustPathSpectralClusteringInstances m_Data;
    /** copy of the original training dataset */
    protected Instances m_TrainsetNew;
    /** copy of the original test dataset */
    protected Instances m_TestsetNew;
    protected ExpDistance expDis;
    private double sigma = 0.43;

    public void setExpDis(ExpDistance expDis) {
        this.expDis = expDis;
    }

    public double getSigma() {
        return sigma;
    }

    @Override
    protected double[] getDistribution(Instance instance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    /**
     * 在相似集合S中寻找相似度最大相似度。
     * @param trainInstances
     * @return
     */
    private double getMaxSimilarityInS(Instances trainInstances){
        
        double maxSimilarity=0.0;
        
        return  maxSimilarity;
    }

    /**
     * 定义训练集中的相似矩阵
     * @param trainInstances 包含有标记的样本和未标记的样本
     * @throws Exception
     */
    private void createSimilarityMatrix(Instances trainInstances) throws Exception {

        int num = trainInstances.numInstances();
        expDis.setSigma(sigma);
        Matrix similarityMatrix = new Matrix(num, num);
        int indexClass = trainInstances.classIndex();
        for (int i = 0; i < num; i++) {
            Instance first = trainInstances.instance(i);
            for (int j = 0; j < num; j++) {
                Instance second = trainInstances.instance(j);
                //如果两个数据都是未标记的数据
                if ((!first.classIsMissing()) & !(second.classIsMissing())) {

                    double dis = 0.0;
                    similarityMatrix.set(i, j, dis);

                }
                //不在以标记点中，（即不在相似S和不相似D集合中，先前给定的标记信息）
                else {
                    if (i == j) {
                        double dis = 0.0;
                        similarityMatrix.set(i, j, dis);

                    } else {
                        double dis = expDis.distance(first, second);
                        similarityMatrix.set(i, j, dis);
                    }
                }


            }
        }
    }

    @Override
    protected void buildClassifier() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void build() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets how the training data will be transformed. Will be one of
     * SMO.FILTER_NORMALIZE, SMO.FILTER_STANDARDIZE, SMO.FILTER_NONE.
     *
     * @return the filtering mode
     */
    public SelectedTag getFilterType() {
        return new SelectedTag(m_filterType, SMO.TAGS_FILTER);
    }

    /**
     * Sets how the training data will be transformed. Should be one of
     * SMO.FILTER_NORMALIZE, SMO.FILTER_STANDARDIZE, SMO.FILTER_NONE.
     *
     * @param value the new filtering mode
     */
    public void setFilterType(SelectedTag value) {
        if (value.getTags() == SMO.TAGS_FILTER) {
            m_filterType = value.getSelectedTag().getID();
        }
    }

    /**
     * Returns the tip text for this property
     *
     * @return 		tip text for this property suitable for
     * 			displaying in the explorer/experimenter gui
     */
    public String filterTypeTipText() {
        return "Determines how/if the data will be transformed.";
    }

    public String getRevision() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TechnicalInformation getTechnicalInformation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Stores the relation between unprocessed instance and processed instance.
     * 将一些数据进行预处理，比如，去掉一些标记，噪声等等
     * @author FracPete (fracpete at waikato dot ac dot nz)
     */
    protected class RobustPathSpectralClusteringInstances
            implements Serializable {

        /** for serialization */
        private static final long serialVersionUID = 1975979462375468594L;
        /** the parent algorithm (used to get the parameters) */
        protected RobustPathSpectralClustering m_Parent = null;
        /** the unprocessed instances */
        protected Instance[] m_Unprocessed = null;
        /** the new training set */
        protected Instances m_Trainset = null;
        /** for finding instances again (used for classifying) */
        protected InstanceComparator m_Comparator = new InstanceComparator(false);
        /** The filter used to make attributes numeric. */
        protected NominalToBinary m_NominalToBinary;
        /** The filter used to standardize/normalize all values. */
        protected Filter m_Filter = null;
        /** The filter used to get rid of missing values. */
        protected ReplaceMissingValues m_Missing;

        /**
         * initializes the object
         *
         * @param parent      the parent algorithm
         * @param train       the train instances
         * @param test        the test instances
         * @throws Exception  if something goes wrong
         */
        public RobustPathSpectralClusteringInstances(RobustPathSpectralClustering parent, Instances train, Instances test)
                throws Exception {

            super();

            m_Parent = parent;

            // set up filters
            m_Missing = new ReplaceMissingValues();
            //只要训练数据的输入结构，
            m_Missing.setInputFormat(train);

            m_NominalToBinary = new NominalToBinary();
            m_NominalToBinary.setInputFormat(train);

            if (getParent().getFilterType().getSelectedTag().getID() == SMO.FILTER_STANDARDIZE) {
                m_Filter = new Standardize();
                m_Filter.setInputFormat(train);
            } //采用SMO过滤有什么作用？
            else if (getParent().getFilterType().getSelectedTag().getID() == SMO.FILTER_NORMALIZE) {
                m_Filter = new Normalize();
                m_Filter.setInputFormat(train);
            } else {
                m_Filter = null;
            }

            // build sorted array (train + test)
            m_Unprocessed = new Instance[train.numInstances() + test.numInstances()];
            for (int i = 0; i < train.numInstances(); i++) {
                m_Unprocessed[i] = train.instance(i);
            }
            for (int i = 0; i < test.numInstances(); i++) {
                m_Unprocessed[train.numInstances() + i] = test.instance(i);
            }
            //把所有的数据集所有的数据都进行了排序！
            Arrays.sort(m_Unprocessed, m_Comparator);

            // filter data，这个时候trainset才有值
            m_Trainset = new Instances(train, 0);
            //成员的训练集是所有的train和测试数据之和？
            for (int i = 0; i < m_Unprocessed.length; i++) {
                m_Trainset.add(m_Unprocessed[i]);
            }
            //这回事真的进行数据过滤
            m_Missing.setInputFormat(m_Trainset);
            m_Trainset = Filter.useFilter(m_Trainset, m_Missing);

            m_NominalToBinary.setInputFormat(m_Trainset);
            m_Trainset = Filter.useFilter(m_Trainset, m_NominalToBinary);

            if (m_Filter != null) {
                m_Filter.setInputFormat(m_Trainset);
                m_Trainset = Filter.useFilter(m_Trainset, m_Filter);
            }
        }

        /**
         * 返回算法父类
         * @return
         */
        private RobustPathSpectralClustering getParent() {
            return m_Parent;
        }

        /**
         * returns the train set (with the processed instances)
         *
         * @return		the train set
         */
        public Instances getTrainSet() {
            return m_Trainset;
        }

        /**
         * returns the number of stored instances
         *
         * @return the number of instances
         */
        public int size() {
            return m_Trainset.numInstances();
        }

        /**
         * returns the index of the given (unprocessed) Instance, -1 in case it
         * can't find the instance
         *
         * @param inst	the instance to return the index for
         * @return		the index for the instance, -1 if not found
         */
        public int indexOf(Instance inst) {
            return Arrays.binarySearch(m_Unprocessed, inst, m_Comparator);
        }

        /**
         * returns the processed instance for the given index, null if not within
         * bounds.
         *
         * @param index	the index of the instance to retrieve
         * @return		null if index out of bounds, otherwise the instance
         */
        public Instance get(int index) {
            if ((index >= 0) && (index < m_Trainset.numInstances())) {
                return m_Trainset.instance(index);
            } else {
                return null;
            }
        }

        /**
         * returns the processed version of the unprocessed instance in the new
         * training set, null if it can't find the instance
         * @param inst      the unprocessed instance to retrieve the processed one
         *                  for
         * @return          the processed version of the given instance
         * @see             #getTrainSet()
         */
        public Instance get(Instance inst) {
            return get(indexOf(inst));
        }
    }
}
