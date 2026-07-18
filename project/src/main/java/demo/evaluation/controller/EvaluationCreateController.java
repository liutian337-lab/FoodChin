package demo.evaluation.controller;

import demo.common.Result;
import demo.evaluation.application.EvaluationApplicationService;
import demo.evaluation.dto.EvaluationCreateDTO;
import demo.evaluation.vo.EvaluationVO;
import demo.infrastructure.ai.FoodFeatureRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/evaluation")
public class EvaluationCreateController {
    private final EvaluationApplicationService evaluationApplicationService;
    public EvaluationCreateController(EvaluationApplicationService evaluationApplicationService) { this.evaluationApplicationService = evaluationApplicationService; }

    @PostMapping("/create")
    public Result<EvaluationVO> create(@Valid @RequestBody EvaluationCreateDTO request) {
        return Result.success(evaluationApplicationService.createEvaluation(request));
    }

    @PostMapping("/predict")
    public Result<EvaluationVO> predictAndCreate(@Valid @RequestBody FoodFeatureRequest request) {
        return Result.success(evaluationApplicationService.createFromPrediction(request));
    }

    @GetMapping("/result/{foodId}")
    public Result<EvaluationVO> findLatestResult(@PathVariable Long foodId) {
        return Result.success(evaluationApplicationService.findLatestByFoodId(foodId));
    }
}
