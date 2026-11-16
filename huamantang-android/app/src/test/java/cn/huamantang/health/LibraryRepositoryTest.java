package cn.huamantang.health;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

public final class LibraryRepositoryTest {
    @Test
    public void categoryFilterReturnsOnlyRequestedCategory() {
        List<Article> result = LibraryRepository.filter("高血压", "");
        assertEquals(1, result.size());
        assertEquals("pressure-at-home", result.get(0).getId());
    }

    @Test
    public void searchChecksTitleAndSummary() {
        assertEquals(1, LibraryRepository.filter("全部", "袖带").size());
        assertTrue(LibraryRepository.filter("全部", "不存在的关键词").isEmpty());
    }

    @Test
    public void unknownIdFallsBackToFirstArticle() {
        assertEquals("glucose-start", LibraryRepository.getById("missing").getId());
    }
}
