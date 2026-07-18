pragma solidity >=0.4.22 <0.7.0;

// Food quality evaluation anchoring contract. It is deployed independently of Trace.
contract EvaluationRegistry {
    struct EvaluationRecord {
        uint256 evaluationId;
        uint256 foodId;
        bytes32 evaluationHash;
        uint256 score;
        string level;
        uint256 confidence;
        string modelVersion;
        uint256 evaluationTime;
        address creator;
    }

    mapping(uint256 => EvaluationRecord) private evaluations;
    mapping(uint256 => uint256) public foodEvaluationCount;
    mapping(uint256 => uint256[]) private foodEvaluationIds;

    uint256 private nextEvaluationId = 1;

    event EvaluationRecorded(
        uint256 indexed evaluationId,
        uint256 indexed foodId,
        bytes32 evaluationHash
    );

    function recordEvaluation(
        uint256 foodId,
        bytes32 evaluationHash,
        uint256 score,
        string memory level,
        uint256 confidence,
        string memory modelVersion,
        uint256 evaluationTime
    ) public returns (uint256 evaluationId) {
        require(foodId > 0, "foodId must be positive");
        require(evaluationHash != bytes32(0), "evaluationHash must not be empty");

        evaluationId = nextEvaluationId;
        nextEvaluationId += 1;

        evaluations[evaluationId] = EvaluationRecord({
            evaluationId: evaluationId,
            foodId: foodId,
            evaluationHash: evaluationHash,
            score: score,
            level: level,
            confidence: confidence,
            modelVersion: modelVersion,
            evaluationTime: evaluationTime,
            creator: msg.sender
        });
        foodEvaluationIds[foodId].push(evaluationId);
        foodEvaluationCount[foodId] += 1;

        emit EvaluationRecorded(evaluationId, foodId, evaluationHash);
        return evaluationId;
    }

    function getEvaluation(uint256 evaluationId) public view returns (
        uint256,
        uint256,
        bytes32,
        uint256,
        string memory,
        uint256,
        string memory,
        uint256,
        address
    ) {
        require(evaluationId > 0 && evaluationId < nextEvaluationId, "evaluationId does not exist");
        EvaluationRecord storage record = evaluations[evaluationId];
        return (
            record.evaluationId,
            record.foodId,
            record.evaluationHash,
            record.score,
            record.level,
            record.confidence,
            record.modelVersion,
            record.evaluationTime,
            record.creator
        );
    }

    function getFoodEvaluations(uint256 foodId) public view returns (uint256[] memory) {
        uint256[] storage storedIds = foodEvaluationIds[foodId];
        uint256[] memory ids = new uint256[](storedIds.length);
        for (uint256 index = 0; index < storedIds.length; index++) {
            ids[index] = storedIds[index];
        }
        return ids;
    }
}
