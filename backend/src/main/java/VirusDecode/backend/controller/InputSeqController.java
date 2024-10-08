package VirusDecode.backend.controller;

import VirusDecode.backend.dto.ReferenceDTO;
import VirusDecode.backend.dto.VarientDTO;
import VirusDecode.backend.service.FastaFileService;
import VirusDecode.backend.service.PythonScriptExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/inputSeq")
public class InputSeqController {

    @Autowired
    private FastaFileService fastaFileService;  // Fasta 파일 처리를 위한 서비스 주입

    // /inputSeq/reference 엔드포인트에 대한 POST 요청 처리
    @PostMapping("/reference")
    public ResponseEntity<Map> getMetadata(@RequestBody ReferenceDTO request) {
        String sequenceId = request.getSequenceId();  // 요청에서 시퀀스 ID 추출
        Map<String, Object> metadata = PythonScriptExecutor.executePythonScriptAsObjectMap("bioinformatics/data/metadata.json","1", sequenceId);

        // GK - 비정상적인 nucleotide 값을 처리하는 부분
        if (metadata == null || metadata.isEmpty()) {
            // 메타데이터가 없는 경우 상태 코드 204 No Content 반환
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        // 메타데이터가 있는 경우 상태 코드 200 OK와 함께 메타데이터 반환
        return ResponseEntity.ok(metadata);
    }

    // /inputSeq/alignment 엔드포인트에 대한 POST 요청 처리
    @PostMapping("/alignment")
    public ResponseEntity<Map<String, Object>> getAlignment(@RequestBody(required = false) VarientDTO request) {
        try {
            // 서비스 호출을 통해 사용자 입력을 파일로 저장
            String savedFilePath = fastaFileService.saveFastaContent(request);

            // 저장된 파일을 이용해 alignment 처리 후 결과 반환
            Map<String, Object> alignmentResult = PythonScriptExecutor.executePythonScriptAsObjectMap("bioinformatics/data/alignment_data.json", "2");

            // 결과를 상태 코드 200 OK와 함께 반환
            return ResponseEntity.ok(alignmentResult);
        } catch (IOException e) {
            // 파일 저장 중 오류가 발생한 경우 상태 코드 500과 함께 오류 메시지 반환
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "파일 저장 중 오류 발생: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}