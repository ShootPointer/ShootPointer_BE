package com.midas.shootpointer.domain.highlight.business.command;

import com.midas.shootpointer.domain.highlight.business.HighlightManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HighlightCommandServiceImplTest {
    @InjectMocks
    private HighlightCommandServiceImpl commandService;

    @Mock
    private HighlightManager highlightManager;

}
