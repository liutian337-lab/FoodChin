package demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blockchain")
public class BlockchainProperties {
    private Webase webase = new Webase();
    private Contract contract = new Contract();
    private Contract evaluationContract = new Contract();
    private Accounts accounts = new Accounts();
    private Network network = new Network();
    private Evaluation evaluation = new Evaluation();

    public String getWebaseUrl() { return webase.getUrl(); }
    public String getContractName() { return contract.getName(); }
    public String getContractAddress() { return contract.getAddress(); }
    public String getContractAbi() { return contract.getAbi(); }
    public String getEvaluationContractName() { return evaluationContract.getName(); }
    public String getEvaluationContractAddress() { return evaluationContract.getAddress(); }
    public String getEvaluationContractAbi() { return evaluationContract.getAbi(); }
    public String getProducerAddress() { return accounts.getProducer(); }
    public String getDistributorAddress() { return accounts.getDistributor(); }
    public String getRetailerAddress() { return accounts.getRetailer(); }
    public int getGroupId() { return network.getGroupId(); }
    public boolean isUseCns() { return network.isUseCns(); }
    public int getTimeoutMs() { return network.getTimeoutMs(); }
    public String getEvaluationAnchorFunction() { return evaluation.getAnchorFunction(); }
    public Webase getWebase() { return webase; }
    public void setWebase(Webase webase) { this.webase = webase; }
    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }
    public Contract getEvaluationContract() { return evaluationContract; }
    public void setEvaluationContract(Contract evaluationContract) { this.evaluationContract = evaluationContract; }
    public Accounts getAccounts() { return accounts; }
    public void setAccounts(Accounts accounts) { this.accounts = accounts; }
    public Network getNetwork() { return network; }
    public void setNetwork(Network network) { this.network = network; }
    public Evaluation getEvaluation() { return evaluation; }
    public void setEvaluation(Evaluation evaluation) { this.evaluation = evaluation; }

    public static class Webase {
        private String protocol;
        private String host;
        private int port;
        private String transactionPath;
        private String url;
        public String getUrl() {
            if (url != null && !url.trim().isEmpty()) return url;
            return protocol + "://" + host + ":" + port + transactionPath;
        }
        public String getProtocol() { return protocol; }
        public void setProtocol(String protocol) { this.protocol = protocol; }
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        public String getTransactionPath() { return transactionPath; }
        public void setTransactionPath(String transactionPath) { this.transactionPath = transactionPath; }
        public void setUrl(String url) { this.url = url; }
    }

    public static class Contract {
        private String name;
        private String address;
        private String abi;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getAbi() { return abi; }
        public void setAbi(String abi) { this.abi = abi; }
    }

    public static class Accounts {
        private String producer;
        private String distributor;
        private String retailer;
        public String getProducer() { return producer; }
        public void setProducer(String producer) { this.producer = producer; }
        public String getDistributor() { return distributor; }
        public void setDistributor(String distributor) { this.distributor = distributor; }
        public String getRetailer() { return retailer; }
        public void setRetailer(String retailer) { this.retailer = retailer; }
    }

    public static class Network {
        private int groupId;
        private boolean useCns;
        private int timeoutMs;
        public int getGroupId() { return groupId; }
        public void setGroupId(int groupId) { this.groupId = groupId; }
        public boolean isUseCns() { return useCns; }
        public void setUseCns(boolean useCns) { this.useCns = useCns; }
        public int getTimeoutMs() { return timeoutMs; }
        public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }
    }

    public static class Evaluation {
        private String anchorFunction;
        public String getAnchorFunction() { return anchorFunction; }
        public void setAnchorFunction(String anchorFunction) { this.anchorFunction = anchorFunction; }
    }
}
