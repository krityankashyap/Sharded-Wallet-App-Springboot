package com.example.ShardedSagaWallet.services.saga;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SagaContext {
 
  private Map<String, Object> contextData;

  public SagaContext(Map<String, Object> contextData){
    this.contextData= contextData!= null ? contextData : new HashMap<>();
  }

  public void put(String key, Object value){
    contextData.put(key, value);
  }

  public Object get(String key){
    return contextData.get(key);
  }

  public Long getLong(String key){  // this function is used to get the value of a key as a Long, if the value is not a Long, it will return null
    Object value= contextData.get(key);

    if( value instanceof Number){
      return ((Number) value).longValue();
    } else {
      return null;
    }
  }

  public BigDecimal getBigDecimal(String key){  // this function is used to get the value of a key as a BigDecimal, if the value is not a BigDecimal, it will return null
    Object value= contextData.get(key);

    if( value instanceof BigDecimal){
      return (BigDecimal) value;
    } else {
      return null;
    }
  }
}
