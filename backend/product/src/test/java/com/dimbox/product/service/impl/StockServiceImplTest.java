package com.dimbox.product.service.impl;

import com.dimbox.common.exception.BizException;
import com.dimbox.product.enums.ProductErrorCode;
import com.dimbox.product.mapper.ProductSkuMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * StockServiceImpl 单元测试。
 * 通过 Mockito mock 掉 Mapper，只验证「库存扣减」的业务分支逻辑，不访问真实数据库。
 */
@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    /** 伪造的 Mapper：不会真的执行 SQL，只返回我们指定的结果。 */
    @Mock
    private ProductSkuMapper productSkuMapper;

    /** 被测对象：Mockito 会把上面的 @Mock 自动注入到它的构造器。 */
    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void deduct_库存不足时抛业务异常() {
        // 模拟 SQL 影响 0 行，即库存不足
        when(productSkuMapper.deductStock(anyLong(), anyInt())).thenReturn(0);

        assertThatThrownBy(() -> stockService.deduct(1L, 2))
                .isInstanceOf(BizException.class)
                .hasMessage(ProductErrorCode.STOCK_NOT_ENOUGH.getMessage());
    }

    @Test
    void deduct_扣减成功时不抛异常() {
        // 模拟 SQL 影响 1 行，即扣减成功
        when(productSkuMapper.deductStock(anyLong(), anyInt())).thenReturn(1);

        assertThatCode(() -> stockService.deduct(1L, 2))
                .doesNotThrowAnyException();
    }

    @Test
    void restore_回补库存时调用Mapper() {
        stockService.restore(1L, 2);

        verify(productSkuMapper).restoreStock(1L, 2);
    }
}
