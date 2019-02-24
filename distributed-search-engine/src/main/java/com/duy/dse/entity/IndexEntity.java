package com.duy.dse.entity;

/**
 * Auto-generated: 2018-09-17 21:5:41
 *索引内容实体类
 * @author duyu
 */
public class IndexEntity {

	/**
	 * id
	 */
    private String id;
	/**
	 * 编码
	 */
    private String code;
	/**
	 * 名称/全称
	 */
    private String name;
	/**
	 * 别名
	 */
    private String aliasName;
	/**
	 * 简拼
	 */
    private String fullSampleSpell;
    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

    public void setCode(String code) {
         this.code = code;
     }
     public String getCode() {
         return code;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setAliasName(String aliasName) {
         this.aliasName = aliasName;
     }
     public String getAliasName() {
         return aliasName;
     }

    public void setFullSampleSpell(String fullSampleSpell) {
         this.fullSampleSpell = fullSampleSpell;
     }
     public String getFullSampleSpell() {
         return fullSampleSpell;
     }
	@Override
	public String toString() {
		return "MaintainIndex [id=" + id + ", code=" + code + ", name=" + name + ", aliasName=" + aliasName
				+ ", fullSampleSpell=" + fullSampleSpell + "]";
	}

     
}