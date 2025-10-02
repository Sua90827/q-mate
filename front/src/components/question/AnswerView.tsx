import React from 'react';
import ShareBtn from './ui/ShareBtn';

type Props = {
  questionText: string;
  myContent: string;
  partnerContent: string;
  nickname: string;
  partnerNickname: string;
};

function AnswerView({ questionText, myContent, partnerContent, nickname, partnerNickname }: Props) {
  const captureID = 'answerView';
  return (
    <div className="pt-[70px] relative flex flex-col items-center w-full h-full sm:w-[400px] sm:h-[550px] bg-secondary rounded-md shadow-md">
      <div id={captureID} className="w-full h-full flex flex-col p-10">
        <p className="text-text-secondary pt-5">#01</p>
        <h2 className="text-24 font-bold">{questionText}</h2>
        <div className="mt-16">
          <div className="pb-6">
            <p className="text-18">{nickname}</p>
            <p className="text-gray-500 text-16">{myContent}</p>
          </div>

          <div>
            <p className="text-18">{partnerNickname}</p>
            <p className="text-gray-500 text-16">{partnerContent}</p>
          </div>
        </div>

        <ShareBtn
          targetId={captureID}
          title={`${questionText} 답변`}
          text={`${nickname}: ${myContent}\n${partnerNickname}: ${partnerContent}`}
        />
      </div>
    </div>
  );
}

export default AnswerView;
