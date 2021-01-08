USE %testdb
GO 

ALTER TABLE "GasQuestionAnswerSet" DROP CONSTRAINT "FK_GasQuestionAnswerSet_GasQuestionSet"
GO

ALTER TABLE "GasQuestionAnswerSet" DROP CONSTRAINT "PK_GasQuestionAnswerSet"
GO

DROP TABLE "GasQuestionAnswerSet"
GO

ALTER TABLE "GasQuestionSet" DROP CONSTRAINT "FK_GasQuestionSet_GasType"
GO

ALTER TABLE "GasQuestionSet" DROP CONSTRAINT "FK_GasQuestionSet_MasterQuestion"
GO

ALTER TABLE "GasQuestionSet" DROP CONSTRAINT "PK_GasQuestionSet"
GO

DROP TABLE "GasQuestionSet"
GO


ALTER TABLE "MasterAnswer" DROP CONSTRAINT "FK_MasterAnswer_MasterQuestion"
GO

--ALTER TABLE "MasterAnswer" DROP CONSTRAINT "PK_MasterAnswer"
GO

DROP TABLE "MasterAnswer"
GO

ALTER TABLE "MasterQuestion" DROP CONSTRAINT "PK_MasterQuestion"
GO

DROP TABLE "MasterQuestion"
GO


ALTER TABLE "GasType" DROP CONSTRAINT "PK_GasType"
GO

DROP TABLE "GasType"
GO


/****** Object:  Table [MasterQuestion]    Script Date: 27/08/2018 08:50:57 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [MasterQuestion](
    [Id] [int] IDENTITY(173,1) NOT NULL,
    [QText] [varchar](255) NULL,
    [QTypeFk] [int] NULL,
    [StoredProcedure] [varchar](255) NULL,
    [BoundColumn] [varchar](255) NULL,
    [ListField] [varchar](255) NULL,
    [Group] [bit] NULL,
    [QuestReference] [varchar](50) NULL,
    [ParentMasterQuestionId] [int] NULL,
    [ErrorMessage] [varchar](255) NULL,
    [ColWidth] [int] NULL,
    [CreatedBy] [varchar](50) NULL,
    [CreatedOn] [datetime] NULL,
    [LastmodifiedBy] [varchar](50) NULL,
    [LastmodifiedOn] [datetime] NULL,
    [ObjectTypeID] [int] NOT NULL,
    [UserDefinedListID] [int] NULL,
    [DestColumn] [varchar](255) NULL,
    [DisplayFunction] [varchar](255) NULL,
    [MaxValueFunction] [varchar](255) NULL,
 CONSTRAINT [PK_MasterQuestion] PRIMARY KEY CLUSTERED 
(
    [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 90) ON [PRIMARY]
) ON [PRIMARY]
GO


/****** Object:  Table [MasterAnswer]    Script Date: 27/08/2018 08:51:27 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [MasterAnswer](
    [Id] [int] IDENTITY(454,1) NOT NULL,
    [QuestionFk] [int] NULL,
    [Answertext] [varchar](500) NULL,
    [PdfAlias] [varchar](50) NULL,
    [Createdby] [varchar](50) NULL,
    [Createdon] [datetime] NULL,
    [Lastmodifiedby] [varchar](50) NULL,
    [Lastmodifiedon] [datetime] NULL,
    [NIpdfAlias] [varchar](50) NULL,
 CONSTRAINT [PK_MasterAnswer] PRIMARY KEY CLUSTERED 
(
    [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 90) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [MasterAnswer]  WITH NOCHECK ADD  CONSTRAINT [FK_MasterAnswer_MasterQuestion] FOREIGN KEY([QuestionFk])
REFERENCES [MasterQuestion] ([Id])
GO

ALTER TABLE [MasterAnswer] CHECK CONSTRAINT [FK_MasterAnswer_MasterQuestion]
GO



/****** Object:  Table [GasType]    Script Date: 27/08/2018 08:51:44 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [GasType](
    [Id] [int] IDENTITY(24,1) NOT NULL,
    [Description] [varchar](50) NULL,
    [IsActive] [bit] NULL,
    [IsFollowupRequired] [bit] NULL,
    [LockingUser] [varchar](50) NULL,
    [LockingTime] [datetime] NULL,
    [Createdby] [varchar](50) NULL,
    [Createdon] [datetime] NULL,
    [Lastmodifiedby] [varchar](50) NULL,
    [Lastmodifiedon] [datetime] NULL,
    [IsActiveForLogging] [bit] NULL,
    [ObjectTypeID] [int] NULL,
 CONSTRAINT [PK_GasType] PRIMARY KEY CLUSTERED 
(
    [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 90) ON [PRIMARY]
) ON [PRIMARY]
GO




/****** Object:  Table [GasQuestionSet]    Script Date: 27/08/2018 08:52:01 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [GasQuestionSet](
    [Id] [int] IDENTITY(1356,1) NOT NULL,
    [GasTypeFk] [int] NULL,
    [MasterQuestionFk] [int] NULL,
    [QuestReference] [varchar](50) NULL,
    [QuesSeq] [int] NULL,
    [Group] [bit] NOT NULL,
    [Required] [bit] NOT NULL,
    [Nextquestion] [int] NULL,
    [LoopQuestion] [bit] NOT NULL,
    [QuestionText] [varchar](255) NULL,
    [ParentGasQuestionId] [int] NULL,
    [ParentGasAnswerId] [int] NULL,
    [ErrorMessage] [varchar](255) NULL,
    [ColWidth] [int] NULL,
    [MenuMasterQuestion] [int] NULL,
    [FollowUpGroup] [bit] NULL,
    [StarsRequired] [bit] NULL,
    [ArenaRequired] [bit] NULL,
    [Createdby] [varchar](50) NULL,
    [Createdon] [datetime] NULL,
    [Lastmodifiedby] [varchar](50) NULL,
    [Lastmodifiedon] [datetime] NULL,
    [DefaultPrevious] [bit] NOT NULL,
 CONSTRAINT [PK_GasQuestionSet] PRIMARY KEY CLUSTERED 
(
    [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 90) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [GasQuestionSet] ADD  CONSTRAINT [DF__Quest_d_b__129420F2]  DEFAULT ((0)) FOR [DefaultPrevious]
GO

ALTER TABLE [GasQuestionSet]  WITH NOCHECK ADD  CONSTRAINT [FK_GasQuestionSet_GasType] FOREIGN KEY([GasTypeFk])
REFERENCES [GasType] ([Id])
GO

ALTER TABLE [GasQuestionSet] CHECK CONSTRAINT [FK_GasQuestionSet_GasType]
GO

ALTER TABLE [GasQuestionSet]  WITH NOCHECK ADD  CONSTRAINT [FK_GasQuestionSet_MasterQuestion] FOREIGN KEY([MasterQuestionFk])
REFERENCES [MasterQuestion] ([Id])
GO

ALTER TABLE [GasQuestionSet] CHECK CONSTRAINT [FK_GasQuestionSet_MasterQuestion]
GO



/****** Object:  Table [GasQuestionAnswerSet]    Script Date: 27/08/2018 08:52:50 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [GasQuestionAnswerSet](
    [Id] [int] IDENTITY(1811,1) NOT NULL,
    [Seq] [int] NULL,
    [AnsFk] [int] NULL,
    [Parentid] [int] NULL,
    [Reportable] [bit] NOT NULL,
    [NextQuestion] [int] NULL,
    [DefaultFlag] [bit] NOT NULL,
    [QuestionFk] [int] NULL,
    [PdfAlias] [varchar](255) NULL,
    [PdfNIAlias] [varchar](255) NULL,
    [Createdby] [varchar](50) NULL,
    [Createdon] [datetime] NULL,
    [Lastmodifiedby] [varchar](50) NULL,
    [Lastmodifiedon] [datetime] NULL,
    [AppActionSetID] [int] NULL,
 CONSTRAINT [PK_GasQuestionAnswerSet] PRIMARY KEY CLUSTERED 
(
    [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 90) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [GasQuestionAnswerSet]  WITH NOCHECK ADD  CONSTRAINT [FK_GasQuestionAnswerSet_GasQuestionSet] FOREIGN KEY([QuestionFk])
REFERENCES [GasQuestionSet] ([Id])
NOT FOR REPLICATION 
GO

ALTER TABLE [GasQuestionAnswerSet] NOCHECK CONSTRAINT [FK_GasQuestionAnswerSet_GasQuestionSet]
GO

