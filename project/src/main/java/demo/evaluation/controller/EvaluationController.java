package demo.evaluation.controller;

import demo.common.Result;
import demo.evaluation.application.EvaluationApplicationService;
import demo.evaluation.dto.EvaluationCreateDTO;
import demo.evaluation.dto.EvaluationPredictDTO;
import demo.evaluation.vo.EvaluationHistoryVO;
import demo.evaluation.vo.EvaluationVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import javax.validation.Valid;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {
    private final EvaluationApplicationService evaluationApplicationService;

    public EvaluationController(EvaluationApplicationService evaluationApplicationService) {
        this.evaluationApplicationService = evaluationApplicationService;
    }

    @GetMapping
    public Result<List<EvaluationVO>> findAll() {
        return Result.success(evaluationApplicationService.findAll());
    }

    @GetMapping("/food/{foodId}")
    public Result<List<EvaluationVO>> findByFoodId(@PathVariable Long foodId) {
        return Result.success(evaluationApplicationService.findByFoodId(foodId));
    }

    @PostMapping
    public Result<EvaluationVO> create(@Valid @RequestBody EvaluationCreateDTO request) {
        return Result.success(evaluationApplicationService.create(request));
    }

    @PostMapping("/predict")
    public Result<EvaluationVO> predictAndCreate(@Valid @RequestBody EvaluationPredictDTO request) {
        return Result.success(evaluationApplicationService.createFromPrediction(request));
    }

    @GetMapping("/{evaluationId}/history")
    public Result<List<EvaluationHistoryVO>> findHistory(@PathVariable Long evaluationId) {
        return Result.success(evaluationApplicationService.findHistory(evaluationId));
    }
}
