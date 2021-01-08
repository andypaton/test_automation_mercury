package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.GasQuestion;
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.FileHelper;

import static mercury.helpers.StringHelper.quote;

@Repository
public class GasQuestionDao {

    @Autowired private DbUtilityHelper dbUtilityHelper;

    @Autowired
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger();

    @SuppressWarnings("unchecked")
    public List<GasQuestion> getGasQuestions(String gasType, String question, String answer, Integer level) throws Exception {
        String sql;
        if (level == 0) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_GAS_QUESTIONS_MASTER.sql");
        } else {
            sql = FileHelper.getResourceFile("sql/select/", "GET_GAS_QUESTIONS_CHILD.sql");
        }

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, GasQuestion.class);

        query.setParameter("gasType", gasType);
        if (level != 0) {
            query.setParameter("question", question);
            query.setParameter("answer", answer);
        }

        logger.debug("getGasQuestions: " + sql.replaceAll(":gasType", quote(gasType)).replaceAll(":question", quote(question)).replaceAll(":answer", quote(answer)));
        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<GasQuestion> getGasQuestionsChildren(String gasType, String question, String answer) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_GAS_QUESTIONS_CHILD.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, GasQuestion.class);

        query.setParameter("gasType", gasType);
        query.setParameter("question", question);
        query.setParameter("answer", answer);

        logger.debug("getGasQuestionsChildren: " + sql.replaceAll(":gasType", quote(gasType)).replaceAll(":question", quote(question)).replaceAll(":answer", quote(answer)));
        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
