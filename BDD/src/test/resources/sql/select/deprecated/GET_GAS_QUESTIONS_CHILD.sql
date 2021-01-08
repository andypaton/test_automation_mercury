WITH alice AS (
SELECT gqs.Id , gqs.QuesSeq,  gqs.QuestionText, ma.Answertext, ma.Id AnswerId, gqas.id gqasid,  gqas.QuestionFk, 
gqas.Parentid, gqs.ParentGasQuestionId, gqs.ParentGasAnswerId, gqs.ErrorMessage, mq.QTypeFk, mq.DestColumn, mq.DisplayFunction, mq.MaxValueFunction FROM
%testdb.GasQuestionSet gqs INNER JOIN %testdb.MasterQuestion mq ON gqs.MasterQuestionFk = mq.Id
INNER JOIN %testdb.GasType gt ON gt.Id = gqs.GasTypeFk 
LEFT JOIN %testdb.MasterAnswer ma ON mq.Id = ma.QuestionFk
LEFT JOIN %testdb.GasQuestionAnswerSet gqas ON gqs.id = gqas.QuestionFk AND ma.id = gqas.AnsFk
WHERE gt.Description = :gasType),
rabbit AS ( Select Id, QuesSeq, QuestionText, Answertext, AnswerId, gqasid, QuestionFk, 
                   Parentid, ParentGasQuestionId, ParentGasAnswerId, ErrorMessage, QTypeFk, DestColumn, DisplayFunction, MaxValueFunction, Level = 0

FROM alice WHERE  QuestionText = :question  and Answertext = :answer
UNION ALL
SELECT alice.Id, alice.QuesSeq, alice.QuestionText, alice.Answertext, alice.AnswerId, alice.gqasid, alice.QuestionFk, 
alice.Parentid, alice.ParentGasQuestionId, alice.ParentGasAnswerId, alice.ErrorMessage, alice.QTypeFk, alice.DestColumn, alice.DisplayFunction, alice.MaxValueFunction, rabbit.Level +1
FROM alice JOIN rabbit 
--on rabbit.ParentGasAnswerId = alice.gqasid
ON alice.ParentGasAnswerId = rabbit.gqasid
-- this needs to change
)
SELECT DISTINCT rabbit.id, rabbit.QuestionText, rabbit.ErrorMessage, rabbit.DestColumn, 
CASE WHEN rabbit.QTypeFk=1 THEN 'Text'
WHEN rabbit.QTypeFk=2 THEN 'Boolean'
WHEN rabbit.QTypeFk=3 THEN 'Dropdown'
WHEN rabbit.QTypeFk=5 THEN 'Numeric' 
WHEN rabbit.QTypeFk=6 THEN 'Button' end QType, rabbit.QuesSeq, DisplayFunction, MaxValueFunction,  
rabbit.level FROM rabbit 
WHERE level = 1
ORDER BY QuesSeq

