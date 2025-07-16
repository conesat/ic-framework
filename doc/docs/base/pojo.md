# Pojo

### DTO
dto 用于入参。如前端参数，可传枚举：
```java
/**
 * @author create by ic gen
 * @date 2023/06/14
 */
@Getter
@Setter
public class DicDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 状态枚举
     */
    @NotNull
    private Status status;
    /**
     * 字典key
     */
    @NotEmpty
    private String dicKey;
    /**
     * 字典值
     */
    private String dicVal;
}

```

```java

/**
 * @author ic generator
 * @date 2023/08/07
 */
@RestController
@RequestMapping(value = Api.API_SYS + "/dic", name ="字典")
@RequireAuth(userType = UserType.SYSTEM_USER)
@RequiredArgsConstructor
public class ApiSysDic extends BasicApi {
    private final DicService dicService;

    /**
     * 编辑或者保存 用dto入参
     */
    @PutMapping(name ="编辑")
    public Response<Void> edit(@Validated DicDTO dto) {
        dicService.edit(dto);
        return Response.success();
    }
}


```


### VO

vo 用于出参如:

```java
/**
 * @author create by ic gen
 * @date 2023/06/14
 */
@Getter
@Setter
public class DicVO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 状态枚举
     */
    @NotNull
    private Status status;
    /**
     * 字典key
     */
    @NotEmpty
    private String dicKey;
    /**
     * 字典值
     */
    private String dicVal;
}

```

```java

/**
 * @author ic generator
 * @date 2023/08/07
 */
@RestController
@RequestMapping(value = Api.API_SYS + "/dic", name ="字典")
@RequireAuth(userType = UserType.SYSTEM_USER)
@RequiredArgsConstructor
public class ApiSysDic extends BasicApi {
    private final DicService dicService;

    /**
     * 获取单个详情
     *
     * @param id [Serializable] *id
     * @return
     */
    @GetMapping(value = "/item/{id}", name = "获取详情")
    public Response<DicVO> detail(@PathVariable("id") Serializable id) {
        return Response.success(dicService.selectById(id, DicVO.class));
    }
}


```