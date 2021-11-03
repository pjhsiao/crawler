package com.ecommerce.crawler.model.resp;

import com.ecommerce.crawler.model.vo.SinyaVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class SinyaResp implements Serializable {
    @JsonProperty("showOption")
    private List<SinyaVO> showOption;
}
