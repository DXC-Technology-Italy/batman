package com.dxc.batman.helper;

/**
 * 
 * Variable names/job execution parameters
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public enum JobExecVarEnum {
	BATCH_JOB_ID("JobID"),
	RET_VALUE("RET_VALUE");
    
	private String value;
 
	JobExecVarEnum(String v) {
        this.value = v;
    }
 
    public String getValue() {
        return value;
    }
    
    public static JobExecVarEnum getTipo(String valueString) {
		for (JobExecVarEnum tc : JobExecVarEnum.values()) {
			if (tc.getValue().equalsIgnoreCase(valueString)) {
				return tc;
			}
		}
		return null;
    }

}
