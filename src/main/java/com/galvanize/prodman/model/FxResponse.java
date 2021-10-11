package com.galvanize.prodman.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class FxResponse {

  private Map<String, Double> quotes;

  @Override
  public String toString() {
    return quotes.toString();
  }
}
