import React from 'react';
import ShareBtn from './ui/ShareBtn';

type Props = {
  questionText: string;
  myContent: string;
  partnerContent: string;
};

function AnswerView({ questionText, myContent, partnerContent }: Props) {
  return (
    <div className="relative p-10 flex flex-col items-center lg:w-[500px] md:w-[450px] w-[350px] h-[550px] bg-secondary rounded-md shadow-md">
      <p className="text-text-secondary pt-5">#01</p>
      <h2 className="text-24 font-bold">{questionText}</h2>
      <div className="mt-16">
        <div className="pb-6">
          <p className="text-18">조용한 유령</p>
          <p className="text-gray-500 text-16">{myContent}</p>
        </div>

        <div>
          <p className="text-18">활기찬 고래</p>
          <p className="text-gray-500 text-16">{partnerContent}</p>
        </div>

        <ShareBtn />
      </div>
    </div>
  );
}

export default AnswerView;
