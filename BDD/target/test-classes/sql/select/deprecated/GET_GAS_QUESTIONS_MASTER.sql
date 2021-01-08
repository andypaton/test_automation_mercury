WITH alice AS (
    SELECT gqs.Id , gqs.QuesSeq,  gqs.QuestionText, ma.Answertext, ma.Id AnswerId, gqas.id gqasid,  gqas.QuestionFk, gqas.Parentid, gqs.ParentGasQuestionId, gqs.ParentGasAnswerId, gqs.ErrorMessage, mq.QTypeFk, mq.DestColumn, mq.DisplayFunction, mq.MaxValueFunction 
    FROM %testdb.GasQuestionSet gqs 
    INNER JOIN %testdb.MasterQuestion mq ON gqs.MasterQuestionFk = mq.Id
    INNER JOIN %testdb.GasType gt ON gt.Id = gqs.GasTypeFk 
    LEFT JOIN %testdb.MasterAnswer ma ON mq.Id = ma.QuestionFk
    LEFT JOIN %testdb.GasQuestionAnswerSet gqas ON gqs.id = gqas.QuestionFk AND ma.id = gqas.AnsFk
    WHERE gt.Description = :gasType 
    AND gqs.ParentGasQuestionId = 0 
)
SELECT DISTINCT alice.id, alice.QuestionText, alice.ErrorMessage, alice.DestColumn, 
CASE WHEN alice.QTypeFk=1 THEN 'Text'
WHEN alice.QTypeFk=2 THEN 'Boolean'
WHEN alice.QTypeFk=3 THEN 'Dropdown'
WHEN alice.QTypeFk=5 THEN 'Numeric' END QType, alice.QuesSeq, alice.DisplayFunction, alice.MaxValueFunction, Level = 0 
FROM alice 
ORDER BY QuesSeq