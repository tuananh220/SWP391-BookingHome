-- Run once on the existing SQL Server database before deploying this flow.
ALTER TABLE StayChangeRequests
    ADD RefundAccountName NVARCHAR(100) NULL,
        RefundBankName NVARCHAR(100) NULL,
        RefundAccountNumber VARCHAR(50) NULL,
        RefundStatus VARCHAR(20) NULL;

ALTER TABLE Payments
    ADD StayChangeRequestID INT NULL;

ALTER TABLE Payments
    ADD CONSTRAINT FK_Payments_StayChangeRequests
    FOREIGN KEY (StayChangeRequestID)
    REFERENCES StayChangeRequests(RequestID);