package demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blockchain")
public class BlockchainProperties {
    private String webaseUrl;
    private String contractName;
    private String contractAddress;
    private String contractAbi;
    private String producerAddress;
    private String distributorAddress;
    private String retailerAddress;
    private int groupId;
    private boolean useCns;
    private int timeoutMs;

    public String getWebaseUrl() { return webaseUrl; }
    public void setWebaseUrl(String webaseUrl) { this.webaseUrl = webaseUrl; }
    public String getContractName() { return contractName; }
    public void setContractName(String contractName) { this.contractName = contractName; }
    public String getContractAddress() { return contractAddress; }
    public void setContractAddress(String contractAddress) { this.contractAddress = contractAddress; }
    public String getContractAbi() { return contractAbi; }
    public void setContractAbi(String contractAbi) { this.contractAbi = contractAbi; }
    public String getProducerAddress() { return producerAddress; }
    public void setProducerAddress(String producerAddress) { this.producerAddress = producerAddress; }
    public String getDistributorAddress() { return distributorAddress; }
    public void setDistributorAddress(String distributorAddress) { this.distributorAddress = distributorAddress; }
    public String getRetailerAddress() { return retailerAddress; }
    public void setRetailerAddress(String retailerAddress) { this.retailerAddress = retailerAddress; }
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public boolean isUseCns() { return useCns; }
    public void setUseCns(boolean useCns) { this.useCns = useCns; }
    public int getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }
}
